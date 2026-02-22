package app.expense.domain.suggestion.detector

import android.content.Context
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.text.nlclassifier.BertNLClassifier
import org.tensorflow.lite.task.text.nlclassifier.BertNLClassifier.BertNLClassifierOptions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileBertClassifier @Inject constructor(
    private val context: Context
) {
    private var classifier: BertNLClassifier? = null

    private fun initClassifier() {
        if (classifier != null) return

        try {
            val baseOptionsBuilder = BaseOptions.builder()
            // MobileBERT is computationally expensive, so we try to use GPU if available
            try {
                baseOptionsBuilder.useGpu()
            } catch (e: Exception) {
                // GPU not supported, fallback to CPU is automatic
            }

            val options = BertNLClassifierOptions.builder()
                .setBaseOptions(baseOptionsBuilder.build())
                .build()

            classifier = BertNLClassifier.createFromFileAndOptions(
                context,
                MODEL_PATH,
                options
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun classify(text: String): String {
        initClassifier()
        return try {
            val results = classifier?.classify(text)
            // BertNLClassifier returns a list of Categories
            val bestCategory = results?.maxByOrNull { it.score }
            bestCategory?.label ?: "Unknown"
        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown"
        }
    }

    companion object {
        private const val MODEL_PATH = "mobilebert.tflite"
    }
}
