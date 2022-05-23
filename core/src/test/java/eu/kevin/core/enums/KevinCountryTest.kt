package eu.kevin.core.enums

import eu.kevin.testcore.base.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
class KevinCountryTest : BaseUnitTest() {

    @Test
    fun `test parse() with null input`() {
        Assert.assertNull(KevinCountry.parse(null))
    }

    @Test
    fun `test parse() with empty input`() {
        Assert.assertNull(KevinCountry.parse(""))
    }

    @Test
    fun `test parse() with unknown input`() {
        Assert.assertNull(KevinCountry.parse("xxx"))
    }

    @Test
    fun `test parse() with uppercase input`() {
        Assert.assertEquals(KevinCountry.POLAND, KevinCountry.parse("PL"))
    }

    @Test
    fun `test parse() with lowercase input`() {
        Assert.assertEquals(KevinCountry.POLAND, KevinCountry.parse("pl"))
    }

    @Test
    fun `test parse() with mix input`() {
        Assert.assertEquals(KevinCountry.POLAND, KevinCountry.parse("Pl"))
    }
}