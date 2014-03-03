/*Copyright (C) 2014  Norbsoft Sp. z o.o.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

his program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package com.norbsoft.pdfconverter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.norbsoft.formserializer.SerializableContent;
import com.norbsoft.pdfconverter.helpers.PDFHelper;

public class MainActivity extends Activity {

	private final static String TAG = "Norbsoft PDF Converter";
	SerializableContent serializableContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = getIntent();
		serializableContent = (SerializableContent)intent.getSerializableExtra("serializableContent");
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    if(serializableContent != null){
			final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "", 
					getResources().getString(R.string.prepare_pdf));
				new Thread(new Runnable() {						
					@Override
					public void run() {
						try{						
							PDFHelper pdfHelper = new PDFHelper(MainActivity.this, serializableContent.getFontUrl());
							int result = pdfHelper.generate(serializableContent.getUrlToSave(), serializableContent.getSign(), serializableContent.getForm(),
										serializableContent.getPhotosArray(), serializableContent.getWorkers());
							progressDialog.dismiss();
							if(result == 1){				    				    
								Intent finishIntent = new Intent();	
								finishIntent.putExtra("fileUrl", pdfHelper.getUrl());
								setResult(Activity.RESULT_OK, finishIntent);
								finish();
							}else{
								finish();
							}				    
						}catch(Exception e){
							progressDialog.dismiss();
							finish();
						}
					}						
				}).start();
		}
	}
}
