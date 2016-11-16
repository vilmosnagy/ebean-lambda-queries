package com.github.vilmosnagy.elq.testproject;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;

public class BaseTest {

    protected EbeanServer server = Ebean.getDefaultServer();

}