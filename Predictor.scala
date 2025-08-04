
import sttp.client4.quick.*
import scala.util.{Success, Failure}
import java.util.Base64
import java.nio.charset.StandardCharsets
import play.api.libs.json._

@main def main() = {
    // NOTE: you must manually set API_KEY below using information retrieved from your IBM Cloud account (https://au-syd.dai.cloud.ibm.com/docs/content/wsj/analyze-data/ml-authentication.html?context=cpdaas)

    val API_KEY = "<your API key>"

    // Get IAM service token
    val iam_url = uri"https://iam.cloud.ibm.com/identity/token?grant_type=urn:ibm:params:oauth:grant-type:apikey&apikey=$API_KEY"
    val iam_response = quickRequest.post(iam_url).header("Content-Type", "application/x-www-form-urlencoded").header("Accept",
        "application/json").send()
    val iamtoken_json: JsValue = Json.parse(iam_response.body)

    val iamtoken = (iamtoken_json \ "access_token").headOption match {
        case Some(JsString(x)) => x
        case _ => ""
    }

    // TODO:  manually define and pass list of values to be scored
    val payload_scoring = Json.stringify(Json.obj("input_data" -> Json.arr(Json.obj(
        "fields" -> Json.arr("list_of_input_fields"),
        "values" -> Json.arr(Json.arr("list_of_values_to_be_scored"))
    ))))

    val scoring_url = uri"https://private.au-syd.ml.cloud.ibm.com/ml/v4/deployments/0ec13077-fbe7-496f-896c-a112073123eb/predictions?version=2021-05-01"

    val response_scoring = quickRequest.post(scoring_url).body(payload_scoring).header("Content-Type",
        "application/json").header("Authorization", "Bearer " + iamtoken).send()
    println("scoring response")
    println(response_scoring.body)
}
