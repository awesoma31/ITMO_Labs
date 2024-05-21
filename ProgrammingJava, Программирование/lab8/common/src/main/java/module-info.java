module org.awesoma.common {
    requires org.apache.commons.cli;
    requires io.github.cdimascio.dotenv.java;
    requires org.apache.logging.log4j;
    exports org.awesoma.common;
    exports org.awesoma.common.exceptions;
    exports org.awesoma.common.commands;
    exports org.awesoma.common.util;
    exports org.awesoma.common.network;
    exports org.awesoma.common.models;
}