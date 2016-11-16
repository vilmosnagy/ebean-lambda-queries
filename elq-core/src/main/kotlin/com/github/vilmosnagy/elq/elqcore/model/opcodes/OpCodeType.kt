package com.github.vilmosnagy.elq.elqcore.model.opcodes

import org.apache.bcel.classfile.JavaClass

internal enum class OpCodeType(val opCode: Int, val otherByteCount: Int = 0) {
    //<editor-fold desc="iload_0">
    iload_0(0x1A) {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.iload_0();
        }
    },
    //</editor-fold>
    //<editor-fold desc="aload_0">
    aload_0(0x2A) {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.aload_0();
        }
    },
    //</editor-fold>
    //<editor-fold desc="aload_1">
    aload_1(0x2B) {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.aload_1();
        }
    },
    //</editor-fold>
    //<editor-fold desc="ldc">
    ldc(0x12, 1)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.ldc(followingBytes[1]);
        }
    },
    //</editor-fold>
    //<editor-fold desc="invokestatic">
    invokestatic(0xB8, 2)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.invokestatic(followingBytes[1], followingBytes[2])
        }
    },
    //</editor-fold>
    //<editor-fold desc="invokevirtual">
    invokevirtual(0xB6, 2)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.invokevirtual(followingBytes[1], followingBytes[2])
        }
    },
    //</editor-fold>
    //<editor-fold desc="invokespecial">
    invokespecial(0xB7, 2)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.invokespecial(followingBytes[1], followingBytes[2])
        }
    },
    //</editor-fold>
    //<editor-fold desc="iconst_0">
    iconst_0(0x03)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.iconst_0()
        }
    },
    //</editor-fold>
    //<editor-fold desc="iconst_1">
    iconst_1(0x04)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.iconst_1()
        }
    },
    //</editor-fold>
    //<editor-fold desc="iconst_2">
    iconst_2(0x05)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.iconst_2()
        }
    },
    //</editor-fold>
    //<editor-fold desc="iconst_3">
    iconst_3(0x06)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.iconst_3()
        }
    },
    //</editor-fold>
    //<editor-fold desc="iconst_4">
    iconst_4(0x07)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.iconst_4()
        }
    },
    //</editor-fold>
    //<editor-fold desc="iconst_5">
    iconst_5(0x08)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.iconst_5()
        }
    },
    //</editor-fold>
    //<editor-fold desc="bipush">
    bipush(0x10, 1)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.bipush(followingBytes[1])
        }
    },
    //</editor-fold>
    //<editor-fold desc="sipush">
    sipush(0x11, 2)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.sipush(followingBytes[1], followingBytes[2])
        }
    },
    //</editor-fold>
    //<editor-fold desc="if_icmpne">
    if_icmpne(0xA0, 2)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.if_icmpne(followingBytes[1], followingBytes[2])
        }
    },
    //</editor-fold>
    //<editor-fold desc="ifnonnull">
    ifnonnull(0xC7, 2)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.ifnonnull(followingBytes[1], followingBytes[2])
        }
    },
    //</editor-fold>
    //<editor-fold desc="ireturn">
    ireturn(0xAC)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.ireturn()
        }
    },
    //</editor-fold>
    //<editor-fold desc="areturn">
    areturn(0xB0)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.areturn()
        }
    },
    //</editor-fold>
    //<editor-fold desc="goto">
    goto(0xA7, 2)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.goto(followingBytes[1], followingBytes[2])
        }
    },
    //</editor-fold>
    //<editor-fold desc="getfield">
    getfield(0xB4, 2)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.getfield(followingBytes[1], followingBytes[2])
        }
    },
    //</editor-fold>
    //<editor-fold desc="dup">
    dup(0x59)  {
        override fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode {
            return OpCode.dup()
        }
    }
    //</editor-fold>
    ;

    abstract fun createNew(followingBytes: List<Int>, bcelClass: JavaClass): OpCode
}