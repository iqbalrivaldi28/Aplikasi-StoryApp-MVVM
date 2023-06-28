package com.example.intermediateiqbal

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.intermediateiqbal.adapter.UserAdapter
import com.example.intermediateiqbal.addStory.AddStoryActivity
import com.example.intermediateiqbal.addStory.AddStoryActivity.Companion.EXTRA_ADD_TOKEN
import com.example.intermediateiqbal.databinding.ActivityMainBinding
import com.example.intermediateiqbal.detailStory.DetailStoryActivity
import com.example.intermediateiqbal.detailStory.DetailStoryActivity.Companion.EXTRA_DATA
import com.example.intermediateiqbal.login.LoginActivity
import com.example.intermediateiqbal.retrofit.response.StoryItem
import com.example.intermediateiqbal.viewmodel.MainViewModel
import com.example.intermediateiqbal.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter : UserAdapter
    private lateinit var token : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]


        mainViewModel.getUser().observe(this){
            if (it.isLogin){
                token = it.token
                mainViewModel.getStory(token)
            }
        }

        binding.rvListStory.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter()
        binding.rvListStory.adapter = adapter

        binding.btnAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            intent.putExtra(EXTRA_ADD_TOKEN, token)
            startActivity(intent)
        }

        adapter.setOnItemClickCallback(object: UserAdapter.OnItemClickCallback{
            override fun onItemClicked(story: StoryItem) {
                val intent = Intent(this@MainActivity, DetailStoryActivity::class.java)
                intent.putExtra(EXTRA_DATA, story)
                startActivity(intent)
            }
        })

        mainViewModel.listStory.observe(this){
            adapter.setListStory(it)
        }
    }

    override fun onResume() {
        super.onResume()

        mainViewModel.getStory(token)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.opttion_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.keluar -> {
                mainViewModel.logout()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.tentang -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}




