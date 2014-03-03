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

import android.content.Context;

public class FileHelper {
	
	 public static boolean fileExist(Context context, String path){
		 File file = new File(path);
		 if(file.exists()){			
			 return true;
		 }else{
			 return false;
		 }
	 }
	 
	 
	 public static boolean deleteFile(String filePath){
		 File file = new File(filePath);
		 boolean deleted = file.delete();
		 return deleted;
	 }
}
