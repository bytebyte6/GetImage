package com.bytebyte6.image

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val searchImage = SearchImageImpl()
        searchImage.searchs("china+country+flag").forEach {
            println(it)
        }
    }
}