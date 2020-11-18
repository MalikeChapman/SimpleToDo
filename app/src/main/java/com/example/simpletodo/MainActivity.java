package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String Key_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CORE = 20;

    List<String> items;
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);



        loadItems();


        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model
                items.remove(position);
                // Notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was Removed", Toast.LENGTH_SHORT).show();
                saveItems();

            }
        };
        ItemsAdapter.onClickListener onClickListener = new ItemsAdapter.onClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("Main Activity", "Single click at position " + position);
                // Create the new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                // pass the data being edited
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(Key_ITEM_POSITION, position);
                // display the activity
                startActivityForResult(i, EDIT_TEXT_CORE);

            }
        };


        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = etItem.getText().toString();
                // Add item to the model
                items.add(todoItem);
                // Notify adapter that an item is inserted
                itemsAdapter.notifyItemInserted(items.size() - 1);
                etItem.setText("");
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data){
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CORE){
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(Key_ITEM_POSITION);
            items.set(position, itemText);
            itemsAdapter.notifyItemChanged(position);
            saveItems();
            Toast.makeText(getApplicationContext(), "Item Updated Successfully", Toast.LENGTH_SHORT).show();

        } else {
            Log.w("Main Activity", "Unknown call to onActivityResult");
        }

    }
    private File getDataFile(){
        return new File(getFilesDir(), "data.txt");
    }

    // This function saves items by writting them into the data file
    private void loadItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items");
            items = new ArrayList<>();
        }
    }
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e){
            Log.e("MainActivity", "Error reading items");


        }
    }
}
