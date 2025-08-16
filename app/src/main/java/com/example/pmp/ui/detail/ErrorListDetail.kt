import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pmp.R
import com.example.pmp.databinding.ActivityErrorListDetailBinding
import com.example.pmp.viewModel.ErrorListDetailVM

class ErrorListDetail : AppCompatActivity() {
    private lateinit var projectId: String
    private lateinit var platform: String
    private lateinit var viewModel: ErrorListDetailVM
    private lateinit var binding: ActivityErrorListDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 使用DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_error_list_detail)
        binding.lifecycleOwner = this

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectId = intent.getStringExtra("projectId").toString()
        platform = intent.getStringExtra("platform").toString()
        Log.d("ErrorListDetail", "projectId: ${projectId} , platform: ${platform}")

        // 初始化ViewModel
        viewModel = ViewModelProvider(this)[ErrorListDetailVM::class.java]
        binding.viewModel = viewModel

        // 设置数据
        viewModel.setData(projectId, platform)

    }


}
