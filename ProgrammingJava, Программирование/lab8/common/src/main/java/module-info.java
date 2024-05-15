module org.awesoma.common {
    requires org.apache.commons.cli;
    requires io.github.cdimascio.dotenv.java;
    exports org.awesoma.common;
    exports org.awesoma.common.exceptions;
    exports org.awesoma.common.commands;
    exports org.awesoma.common.util;
    exports org.awesoma.common.network;
    exports org.awesoma.common.models;
}