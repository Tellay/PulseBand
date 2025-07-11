module com.pulseband.pulseband {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires jbcrypt;
    requires org.eclipse.paho.client.mqttv3;
    requires static lombok;
    requires org.postgresql.jdbc;

    opens com.pulseband.pulseband to javafx.fxml;
    exports com.pulseband.pulseband;
    opens com.pulseband.pulseband.controllers to javafx.fxml;
    exports com.pulseband.pulseband.controllers to javafx.fxml;
    exports com.pulseband.pulseband.dtos;
    opens com.pulseband.pulseband.db to javafx.base;
}