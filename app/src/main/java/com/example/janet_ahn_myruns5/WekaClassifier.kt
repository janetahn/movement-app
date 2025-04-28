package com.example.janet_ahn_myruns5

class WekaClassifier {
    @Throws(Exception::class)
    fun classify(i: Array<Double>): Double {
        var p = Double.NaN
        p = N682a9fbe0(i)
        return p
    }

    private fun N682a9fbe0(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[0] == null) {
            p = 0.0
        } else if ((i[0] as Double?)!!.toDouble() <= 110.672015) {
            p = N731b5f391(i)
        } else if ((i[0] as Double?)!!.toDouble() > 110.672015) {
            p = N551da6d64(i)
        }
        return p
    }

    private fun N731b5f391(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[0] == null) {
            p = 0.0
        } else if ((i[0] as Double?)!!.toDouble() <= 86.387993) {
            p = 0.0
        } else if ((i[0] as Double?)!!.toDouble() > 86.387993) {
            p = N154f2c52(i)
        }
        return p
    }

    private fun N154f2c52(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[1] == null) {
            p = 1.0
        } else if ((i[1] as Double?)!!.toDouble() <= 25.177673) {
            p = N69f603393(i)
        } else if ((i[1] as Double?)!!.toDouble() > 25.177673) {
            p = 0.0
        }
        return p
    }

    private fun N69f603393(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[4] == null) {
            p = 0.0
        } else if ((i[4] as Double?)!!.toDouble() <= 9.353713) {
            p = 0.0
        } else if ((i[4] as Double?)!!.toDouble() > 9.353713) {
            p = 1.0
        }
        return p
    }

    private fun N551da6d64(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[64] == null) {
            p = 1.0
        } else if ((i[64] as Double?)!!.toDouble() <= 24.396242) {
            p = N7c56c9db5(i)
        } else if ((i[64] as Double?)!!.toDouble() > 24.396242) {
            p = 2.0
        }
        return p
    }

    private fun N7c56c9db5(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[3] == null) {
            p = 1.0
        } else if ((i[3] as Double?)!!.toDouble() <= 69.56357) {
            p = N5a32de4e6(i)
        } else if ((i[3] as Double?)!!.toDouble() > 69.56357) {
            p = N1a8611ca15(i)
        }
        return p
    }

    private fun N5a32de4e6(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[11] == null) {
            p = 1.0
        } else if ((i[11] as Double?)!!.toDouble() <= 8.464121) {
            p = N384e1fa7(i)
        } else if ((i[11] as Double?)!!.toDouble() > 8.464121) {
            p = N439bf69013(i)
        }
        return p
    }

    private fun N384e1fa7(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[0] == null) {
            p = 1.0
        } else if ((i[0] as Double?)!!.toDouble() <= 281.653327) {
            p = N3b97cc4a8(i)
        } else if ((i[0] as Double?)!!.toDouble() > 281.653327) {
            p = 1.0
        }
        return p
    }

    private fun N3b97cc4a8(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[19] == null) {
            p = 1.0
        } else if ((i[19] as Double?)!!.toDouble() <= 0.952152) {
            p = 1.0
        } else if ((i[19] as Double?)!!.toDouble() > 0.952152) {
            p = N7145d2f29(i)
        }
        return p
    }

    private fun N7145d2f29(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[20] == null) {
            p = 0.0
        } else if ((i[20] as Double?)!!.toDouble() <= 0.760457) {
            p = 0.0
        } else if ((i[20] as Double?)!!.toDouble() > 0.760457) {
            p = N3dc9b23a10(i)
        }
        return p
    }

    private fun N3dc9b23a10(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[29] == null) {
            p = 1.0
        } else if ((i[29] as Double?)!!.toDouble() <= 0.882115) {
            p = 1.0
        } else if ((i[29] as Double?)!!.toDouble() > 0.882115) {
            p = N19e60f4811(i)
        }
        return p
    }

    private fun N19e60f4811(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[64] == null) {
            p = 0.0
        } else if ((i[64] as Double?)!!.toDouble() <= 6.302075) {
            p = N1c1675a212(i)
        } else if ((i[64] as Double?)!!.toDouble() > 6.302075) {
            p = 1.0
        }
        return p
    }

    private fun N1c1675a212(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[27] == null) {
            p = 1.0
        } else if ((i[27] as Double?)!!.toDouble() <= 0.896948) {
            p = 1.0
        } else if ((i[27] as Double?)!!.toDouble() > 0.896948) {
            p = 0.0
        }
        return p
    }

    private fun N439bf69013(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[32] == null) {
            p = 0.0
        } else if ((i[32] as Double?)!!.toDouble() <= 2.523797) {
            p = 0.0
        } else if ((i[32] as Double?)!!.toDouble() > 2.523797) {
            p = N33b3d4bb14(i)
        }
        return p
    }

    private fun N33b3d4bb14(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[26] == null) {
            p = 2.0
        } else if ((i[26] as Double?)!!.toDouble() <= 4.645085) {
            p = 2.0
        } else if ((i[26] as Double?)!!.toDouble() > 4.645085) {
            p = 1.0
        }
        return p
    }

    private fun N1a8611ca15(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[10] == null) {
            p = 1.0
        } else if ((i[10] as Double?)!!.toDouble() <= 15.787186) {
            p = N3512cc5d16(i)
        } else if ((i[10] as Double?)!!.toDouble() > 15.787186) {
            p = 2.0
        }
        return p
    }

    private fun N3512cc5d16(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[26] == null) {
            p = 2.0
        } else if ((i[26] as Double?)!!.toDouble() <= 3.686452) {
            p = 2.0
        } else if ((i[26] as Double?)!!.toDouble() > 3.686452) {
            p = 1.0
        }
        return p
    }
}