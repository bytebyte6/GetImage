package com.bytebyte6.image

import com.google.gson.Gson
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

interface Strategy {
    fun result(doc: Document): String
    fun results(doc: Document): List<String>
}

object IuscStrategy : Strategy {
    private val gson = Gson()
    override fun result(doc: Document): String {
        val iusc = doc.getElementsByClass("iusc")
        iusc.forEach {
            val json = it.attr("m")
            val bingImage = gson.fromJson(json, BingImage::class.java)
            return bingImage.murl
        }
        return ""
    }

    override fun results(doc: Document): List<String> {
        val list = mutableListOf<String>()
        val iusc = doc.getElementsByClass("iusc")
        iusc.forEach {
            val json = it.attr("m")
            val bingImage = gson.fromJson(json, BingImage::class.java)
            list.add(bingImage.turl)
            list.add(bingImage.murl)
        }
        return list
    }
}

object ImgStrategy : Strategy {
    override fun result(doc: Document): String {
        val imgs = doc.getElementsByTag("img")
        for (src in imgs) {
            val image = src.attr("abs:src")
            val width = src.attr("width")
            val height = src.attr("height")
            if (image.isNotEmpty() && width.isNotEmpty() && width.toInt() > 100 && height.isNotEmpty() && height.toInt() > 100) {
                return image
            }
        }
        return ""
    }

    override fun results(doc: Document): List<String> {
        val list = mutableListOf<String>()
        val imgs = doc.getElementsByTag("img")
        for (src in imgs) {
            val image = src.attr("abs:src")
            val width = src.attr("width")
            val height = src.attr("height")
            if (image.isNotEmpty() && width.isNotEmpty() && width.toInt() > 100 && height.isNotEmpty() && height.toInt() > 100) {
                list.add(image)
            }
        }
        return list
    }
}

class SearchImageImpl : SearchImage {

    override val strategys: List<Strategy> = mutableListOf(ImgStrategy, IuscStrategy)

    override val urls: List<UrlProvider> = mutableListOf(BingUrlProvider())

    override fun search(key: String): String {
        urls.forEach { urlProvider ->
            val url = urlProvider.provide(key)
            val doc = Jsoup.connect(url).get()
            strategys.forEach {
                val result = it.result(doc)
                if (result.isNotEmpty()) {
                    return result
                }
            }
        }
        return ""
    }

    override fun searchs(key: String): List<String> {
        val list = mutableListOf<String>()
        urls.forEach { urlProvider ->
            val url = urlProvider.provide(key)
            val doc = Jsoup.connect(url).get()
            strategys.forEach {
                val result = it.results(doc)
                if (result.isNotEmpty()) {
                    list.addAll(result)
                }
            }
        }
        return list
    }
}

interface SearchImage {
    val strategys: List<Strategy>

    val urls: List<UrlProvider>

    fun search(key: String): String

    fun searchs(key: String): List<String>
}

interface UrlProvider {
    fun provide(key: String): String
}

class BingUrlProvider : UrlProvider {
    override fun provide(key: String): String {
        return "https://cn.bing.com/images/search?q=${key.replace("&"," ")}"
    }
}