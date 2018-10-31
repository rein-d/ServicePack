package corp.remotehelp.integration.evotor.ru


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import corp.remotehelp.integration.evotor.ru.databinding.FragmentStartBinding
import ru.evotor.framework.users.UserApi


class StartFragment : Fragment() {

    lateinit var viewModel: StartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(StartViewModel::class.java)
        viewModel.init(requireActivity() as MainActivity)
        val user = UserApi.getAuthenticatedUser(requireContext())
        viewModel.name.value = user?.firstName
        viewModel.phone.value = user?.phone
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentStartBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        return binding.root
    }
}
