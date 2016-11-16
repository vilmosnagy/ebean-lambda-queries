package com.github.vilmosnagy.elq.elqcore.agent

import java.lang.instrument.Instrumentation

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
object JavaAgent {

    lateinit var inst: Instrumentation

    @JvmStatic
    fun agentmain(agentArgs: String?, inst: Instrumentation) {
        System.out.println(agentArgs);
        System.out.println("Hi from the agent!");
        System.out.println("I've got instrumentation!: " + inst);
        this.inst = inst
    }
}