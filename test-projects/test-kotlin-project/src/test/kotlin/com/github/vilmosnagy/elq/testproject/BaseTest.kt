package com.github.vilmosnagy.elq.testproject

import com.avaje.ebean.Ebean
import com.avaje.ebean.EbeanServer
import com.avaje.ebean.EbeanServerFactory
import com.avaje.ebean.config.ServerConfig
import io.kotlintest.specs.FeatureSpec
import org.junit.Before
import org.junit.BeforeClass
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

var isSqlRun = false;

open class BaseTest : FeatureSpec() {

    val server = Ebean.getDefaultServer()

    fun ignored_scenario(message: String, testMethod: () -> Unit) = Unit

}