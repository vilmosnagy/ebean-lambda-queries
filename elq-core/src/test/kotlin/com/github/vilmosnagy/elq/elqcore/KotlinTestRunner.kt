package com.github.vilmosnagy.elq.elqcore

import de.jodamob.kotlin.testrunner.configureClassOpeningClassLoader
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.TestBase
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

/**
 * @author Vilmos Nagy  <vilmos.nagy@outlook.com>
 */
class KotlinTestRunner(klass: Class<TestBase>) : Runner() {
    val runner: KTestJUnitRunner = KTestJUnitRunner(configureClassOpeningClassLoader(klass) as Class<TestBase>)

    override fun run(notifier: RunNotifier?) {
        runner.run(notifier)
    }

    override fun getDescription(): Description? {
        return runner.description
    }

}