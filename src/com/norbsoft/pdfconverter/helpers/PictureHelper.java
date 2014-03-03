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

package com.norbsoft.pdfconverter.helpers;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PictureHelper {

	public static Bitmap bitmapFromUrl(String url){
		File imgFile = new  File(url);
		if(imgFile.exists()){
		    return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		}
		return null;
	}
}
