module com.lrogerscs.marketlynx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.lrogerscs.marketlynx to javafx.fxml;
    exports com.lrogerscs.marketlynx;
    exports com.lrogerscs.marketlynx.controller;
    opens com.lrogerscs.marketlynx.controller to javafx.fxml;
    exports com.lrogerscs.marketlynx.pane;
    opens com.lrogerscs.marketlynx.pane to javafx.fxml;
    exports com.lrogerscs.marketlynx.stock;
    opens com.lrogerscs.marketlynx.stock to javafx.fxml;
    exports com.lrogerscs.marketlynx.downloader;
    opens com.lrogerscs.marketlynx.downloader to javafx.fxml;
    exports com.lrogerscs.marketlynx.reader;
    opens com.lrogerscs.marketlynx.reader to javafx.fxml;
}