package org.awesoma.server;

import com.jcraft.jsch.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.common.Environment;
import org.awesoma.common.commands.*;
import org.awesoma.common.exceptions.EnvVariableNotFoundException;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.util.Validator;
import org.awesoma.server.exceptions.NoConnectionException;
import org.awesoma.server.managers.ClientHandler;
import org.awesoma.server.managers.CollectionManager;
import org.awesoma.server.managers.CommandInvoker;
import org.awesoma.server.managers.DBManager;
import org.awesoma.server.util.json.DumpManager;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

public class TCPServer {
    private static final Logger logger = LogManager.getLogger(TCPServer.class);
    private static final String PATH = System.getenv(Environment.ENV);
    private final String host;
    private final int port;
    private CommandInvoker commandInvoker;
    private DumpManager dumpManager;
    private CollectionManager collectionManager;
    private Session sshSession;

    public TCPServer(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            DBManager db = new DBManager();
            registerCommands();
            dumpManager = new DumpManager(PATH, new Validator());
            collectionManager = new CollectionManager(dumpManager);
            commandInvoker = new CommandInvoker(collectionManager, dumpManager, db);
        } catch (EnvVariableNotFoundException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        } catch (ValidationException e) {
            System.err.println("Collection validation failed: " + e.getLocalizedMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
            if (commandInvoker != null) {
                commandInvoker.visit(new ExitCommand());
            }
            System.exit(1);
        } catch (SQLException e) {
//            System.exit(1);
            throw new RuntimeException(e);
        }
    }

    private void prepareSSH() {

    }

    public void run() throws BindException {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.configureBlocking(false);
            logger.info("Server started: " + serverSocketChannel.getLocalAddress());

            interactive(serverSocketChannel);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        } finally {
            logger.info("Server stopped");
        }
    }

    private void interactive(ServerSocketChannel serverSocketChannel) throws IOException {
        while (true) {
            try (var selector = Selector.open()) {
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                serverSocketChannel.configureBlocking(false);

                logger.info("Awaiting client");
                while (true) {
                    try {
                        selector.selectNow();
                        Set<SelectionKey> selectedKeys = selector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            if (key.isAcceptable()) {
                                SocketChannel clientChannel = serverSocketChannel.accept();
                                if (clientChannel != null) {
                                    clientChannel.configureBlocking(false);
                                    clientChannel.register(selector, SelectionKey.OP_READ);
                                    logger.info("Client connected: " + clientChannel.getRemoteAddress());
                                }
                            } else if (key.isReadable()) {
                                var clientChannel = (SocketChannel) key.channel();
                                if (clientChannel != null) {
                                    new Thread(new ClientHandler(commandInvoker, clientChannel)).start();
                                    clientChannel.register(selector, SelectionKey.OP_WRITE);
                                }
                            }
                            keyIterator.remove();
                        }
                    } catch (NoConnectionException e) {
                        logger.error(e + " while selecting");
                        commandInvoker.visit(new ExitCommand());
                        break;
                    }
                }
            } catch (ClosedChannelException e) {
                logger.error(e + " while processing");
                break;
            } catch (IOException | RuntimeException e) {
                logger.error(e);
                break;
            } finally {
                commandInvoker.visit(new ExitCommand());
                logger.info("Selector closed");
            }
        }
    }

    public void closeConnection() {
        // todo
    }

    public DumpManager getDumpManager() {
        return dumpManager;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    private void registerCommands() {
        Environment.register(new HelpCommand());
        Environment.register(new ShowCommand());
        Environment.register(new ExitCommand());
        Environment.register(new AddCommand());
        Environment.register(new InfoCommand());
        Environment.register(new ClearCommand());
        Environment.register(new SortCommand());
        Environment.register(new PrintFieldAscendingTBOCommand());
        Environment.register(new UpdateIdCommand());
        Environment.register(new RemoveByIdCommand());
        Environment.register(new RemoveAtCommand());
        Environment.register(new AddIfMaxCommand());
    }
}