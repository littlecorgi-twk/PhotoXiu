package com.littlecorgi.puzzle.util

import com.littlecorgi.puzzle.bean.Procedure

class ProcedureUtil {
    companion object {
        private var procedureList: ArrayList<Procedure>? = null

        fun getInstance(): ArrayList<Procedure> {
            if (procedureList == null) {
                synchronized(ProcedureUtil::class) {
                    if (procedureList == null) {
                        procedureList = ArrayList()
                    }
                }
            }
            return this.procedureList!!
        }
    }
}