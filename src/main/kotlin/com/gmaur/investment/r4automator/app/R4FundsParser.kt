package com.gmaur.investment.r4automator.app

import com.gmaur.investment.r4automator.infrastructure.files.FilePickerProvider
import com.gmaur.investment.r4automator.infrastructure.files.FileUtils
import com.gmaur.investment.r4automator.infrastructure.funds.ParseFunds
import java.nio.file.Paths

class R4FundsParser {

    private val filePickerProvider: FilePickerProvider = FilePickerProvider.aNew()

    fun run(args: Array<String>) {

        val file = filePickerProvider.request()
        val funds = ParseFunds(FileUtils.readAllLinesAsString(Paths.get(file))).run()
        for (fund in funds) {
            println(fund)
        }
    }
}


fun main(args: Array<String>) {
    R4FundsParser().run(args)
}