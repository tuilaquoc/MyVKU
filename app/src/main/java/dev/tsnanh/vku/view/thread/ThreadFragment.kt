package dev.tsnanh.vku.view.thread

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dev.tsnanh.vku.R
import dev.tsnanh.vku.adapters.ThreadAdapter
import dev.tsnanh.vku.adapters.ThreadClickListener
import dev.tsnanh.vku.databinding.FragmentThreadBinding
import timber.log.Timber

class ThreadFragment : Fragment() {

    private lateinit var viewModel: ThreadViewModel
    private lateinit var binding: FragmentThreadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_thread, container, false)

        val args: ThreadFragmentArgs by navArgs()
        binding.toolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            title = args.title
        }

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val navArgs: ThreadFragmentArgs by navArgs()

        viewModel = ViewModelProvider(
            this,
            ThreadViewModelFactory(navArgs.id)
        ).get(ThreadViewModel::class.java)

        configureList()
        val adapter = ThreadAdapter(ThreadClickListener {
            Timber.d("ThreadClickListener called: $it")
        })
        binding.listThread.adapter = adapter

        viewModel.threads.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
    }

    private fun configureList() {
        binding.listThread.setHasFixedSize(true)
        binding.listThread.layoutManager = LinearLayoutManager(requireContext())
    }
}