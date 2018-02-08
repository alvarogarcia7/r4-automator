package com.gmaur.investment.r4automator.infrastructure.funds

import com.gmaur.investment.r4automator.app.UserInteraction
import com.gmaur.investment.r4automator.domain.ISIN
import com.gmaur.investment.r4automator.infrastructure.files.FileUtils
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import java.math.BigDecimal

class FundsBuyerPage(private val driver: WebDriver, private val userInteraction: UserInteraction, private val fundsConfiguration: FundsConfiguration) {
    private val fromFundsAccount = By.cssSelector("#fondos-options > td:nth-child(1)")

    fun buy(purchaseOrder: PurchaseOrder) {
        navigateToTheFundsPage()
        selectFund(purchaseOrder.isin)
        if (impossibleToSelectFromFunds()) {
            println("Impossible to fulfill this purchase order" + purchaseOrder)
            return
        }
        selectFromFundsAccount()
        selectAmount(purchaseOrder.amount)
        acceptAllConditions()
        if (userInteraction.`confirm?`("confirm this operation?")) {
            confirmButton().click()
            savePageSource()
        }
    }

    private fun navigateToTheFundsPage() {
        driver.get(fundsConfiguration.fundsurl)
    }

    private fun impossibleToSelectFromFunds(): Boolean {
        val fromFundsAccount = driver.findElement(fromFundsAccount)
        return fromFundsAccount.getAttribute("class").contains("no-operativa")
    }

    private fun savePageSource() {
        FileUtils.saveTemporaryFile(driver.pageSource)
    }

    data class PurchaseOrder(val isin: ISIN, val amount: BigDecimal)

    private fun acceptAllConditions() {
        val tables = driver.findElements(By.cssSelector("form > table"))

        val documentation = tables.first()

        val previousPage = driver.windowHandle

        // opens in a new page
        acceptDocumentation(documentation).click()

        goBackTo(previousPage)

        acceptDisclaimers(tables)
    }

    private fun acceptDisclaimers(tables: MutableList<WebElement>) {
        val disclaimers = tables.last()

        //click on all disclaimers
        disclaimers.findElements(By.cssSelector("input[type='checkbox']")).forEach({ it.click() })
    }

    private fun goBackTo(previousPage: String?) {
        driver.switchTo().window(previousPage)
    }

    private fun selectAmount(amount: BigDecimal) {
        typeAmount(amount)
        confirmButton().click()
    }

    private fun typeAmount(amount: BigDecimal) {
        driver.findElement(By.id("IMPORTE_FONDO_1")).sendKeys(amount.toString())
    }

    private fun confirmButton() = driver.findElement(By.id("B_ENVIAR_ORD"))

    private fun selectFromFundsAccount() {
        val fromFundsAccount = driver.findElement(fromFundsAccount)
        if (fromFundsAccount.findElement(By.tagName("a")).isEnabled) {
            fromFundsAccount.click()
        }
    }

    private fun selectFund(isin: ISIN) {
        driver.findElement(By.cssSelector("tr[data-isin='" + isin.value + "']")).findElements(By.tagName("a")).last().click()
    }

    private fun acceptDocumentation(table: WebElement) = table.findElements(By.tagName("a")).last()
}