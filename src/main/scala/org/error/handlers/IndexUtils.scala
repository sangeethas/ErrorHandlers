package org.error.handlers

import org.apache.http.HttpHost
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback
import org.elasticsearch.client.{RequestOptions, RestClient, RestHighLevelClient}
import org.elasticsearch.common.settings.Settings

object IndexUtils {

  val credentialsProvider = new BasicCredentialsProvider
  credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "elastic"))
  val builder = RestClient.builder(new HttpHost("localhost", 9200, "http"))
  builder.setHttpClientConfigCallback(new HttpClientConfigCallback() {
    override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
  })
  var client = new RestHighLevelClient(builder)

  def readonly() = {
    println("making the index read only")
    val request = new UpdateSettingsRequest("pds-payments1");
    val settingKey = "index.blocks.read_only"
    val settingValue = true
    val settings = Settings.builder.put(settingKey, settingValue).build
    request.settings(settings)

    val updateSettingsResponse =
      client.indices().putSettings(request, RequestOptions.DEFAULT);
    println("index made readonly: " + updateSettingsResponse.isAcknowledged)

  }

  def forcemerge() = {
    println("forcemerging the index")
    val request = new ForceMergeRequest("pds-payments1")
    request.maxNumSegments(1)
    val forceMergeResponse = client.indices.forcemerge(request, RequestOptions.DEFAULT)
    println("index forcemerged")
  }

}
