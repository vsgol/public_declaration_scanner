class Root {
    class A {
        class A1 {
            fun f1() {}
        }
        class A2
    }

    class B
    class C {
        class C1
        class C2 {
            fun inner() {}
        }
    }

    class D {
        fun d1() {}
        fun d2() {}
    }

    class E

    fun topLevelFun() {}
}
