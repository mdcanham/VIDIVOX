package com.vidivox.controller;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import com.vidivox.view.WarningDialogue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class AboutVidivoxController implements Initializable {

    @FXML
    private Hyperlink creativeCommonsHyperlink = new Hyperlink();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        creativeCommonsHyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String url = "http://creativecommons.org/licenses/by-nc-sa/4.0/";

                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("xdg-open " + url);
                } catch (IOException e) {
                    new WarningDialogue("We can't open the default browser on your system. You will have to copy and paste the link manually, sorry. http://creativecommons.org/licenses/by-nc-sa/4.0/");
                }

            }
        });

    }
}
