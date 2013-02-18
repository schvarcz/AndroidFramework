package com.example.androidframework;

import java.util.ArrayList;

import com.example.model.ExemploTabela;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	protected TextView TVNome;
	protected Button BTNovo, BTSearch, BTSearchAll;
	protected EditText ETNome;
	protected boolean rodou=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TVNome = (TextView)findViewById(R.id.TVNome);
        BTNovo = (Button)findViewById(R.id.BTNovo);
        BTSearch = (Button)findViewById(R.id.BTSearch);
        BTSearchAll = (Button)findViewById(R.id.BTSearchAll);
        ETNome = (EditText)findViewById(R.id.ETNome);
        
        
        BTNovo.setOnClickListener(salvarNovo);
        BTSearch.setOnClickListener(searchClick);
        BTSearchAll.setOnClickListener(searchAllClick);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	private OnClickListener salvarNovo = new OnClickListener() {
		
		public void onClick(View v) {
			ExemploTabela tb = new ExemploTabela();
			tb.setNomeQualquer(ETNome.getText().toString());
			if(tb.save())
			{
				TVNome.setText("Salvo!");
			}
			else
			{
				TVNome.setText("Problemas a vista");
			}
		}
	};
	

	private OnClickListener searchClick = new OnClickListener() {
		
		public void onClick(View v) {
			ExemploTabela tb = new ExemploTabela();
			if(!ETNome.getText().toString().equals(""))
				tb.setNomeQualquer(ETNome.getText().toString());
			ArrayList<ExemploTabela> resp = tb.findAllByAttributes();
			setText(resp);
			
		}
	};
	private OnClickListener searchAllClick = new OnClickListener() {
		
		public void onClick(View v) {

			ExemploTabela tb = new ExemploTabela();
			ArrayList<ExemploTabela> resp = tb.findAll();
			setText(resp);
		}
	};
	
	public void setText(ArrayList<ExemploTabela> resp)
	{
		String str = "";
		for(ExemploTabela et:resp)
		{
			if(!str.equals(""))
				str+="\n";
			str += et.toString();
		}
		
		TVNome.setText(str);
	}
}
