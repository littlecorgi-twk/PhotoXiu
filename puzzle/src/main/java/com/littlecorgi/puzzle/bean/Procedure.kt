package com.littlecorgi.puzzle.bean

class Procedure {

    private var name: String? = null
        get() = field
        set(_name) {
            field = _name
        }

    private var value: Float = 0.0F
        get
        set(_value) {
            field = _value
        }

    constructor()

    constructor(name: String?, value: Float) {
        this.name = name
        this.value = value
    }

    override fun toString(): String {
        return "$name -> $value"
    }
}