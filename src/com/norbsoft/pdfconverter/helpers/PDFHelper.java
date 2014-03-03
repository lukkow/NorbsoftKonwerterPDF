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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.norbsoft.formserializer.Form;
import com.norbsoft.formserializer.SerializableBitmap;

public class PDFHelper {	

	Context context;
	final static String TAG ="PDFHelper";
	String url;
	BaseFont baseFont;
	Font normal;
	Font bold;
	Font strike;
	
	public PDFHelper(Context context, String fontUrl){		
		this.context = context;					
		try {
			baseFont = BaseFont.createFont(fontUrl, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		} catch (DocumentException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		normal = new Font(baseFont, 10 ,Font.NORMAL);	
		bold = new Font(baseFont, 10 ,Font.BOLD);
		strike = new Font(baseFont, 10, Font.STRIKETHRU);	
				
	}
	
	public String getUrl(){
		return url;
	}
	
	public File getFile(){		
		return new File(url);
	}
	
	public int generate(String saveUrl, SerializableBitmap sign, Form form, ArrayList<String> photos, ArrayList<String> workers){		
		try {
			Document document = new Document();				
			
			url = saveUrl;
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(url));			
			document.setMargins(20, 20, 20, 20);
			document.open();		
						
			Bitmap inputLogo = BitmapFactory.decodeStream(context.getAssets().open("logo.jpg"));			
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			inputLogo.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
			Image logo = Image.getInstance(outstream.toByteArray());
			
			Bitmap inputQR = BitmapFactory.decodeStream(context.getAssets().open("qr.jpg"));			
			outstream = new ByteArrayOutputStream();
			inputQR.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
			Image qr = Image.getInstance(outstream.toByteArray());
			
			outstream = new ByteArrayOutputStream();
			sign.getImage().compress(Bitmap.CompressFormat.JPEG, 100, outstream);
			Image sgn = Image.getInstance(outstream.toByteArray());
			
			qr.scaleAbsolute(90f, 90f);
			qr.setAbsolutePosition(340f, 705f);
			
			logo.scaleAbsolute(150f, 50f);
			logo.setAbsolutePosition(50f, 750f);
			
			sgn.scaleAbsolute(130f, 30f);
			sgn.setAbsolutePosition(25f, 75f);
			
			Paragraph p = new Paragraph("\r\n\r\n\r\nMPWiK S.A.\r\n50-421 Wroc³aw\r\nul.Na Grobli 14-16", normal);
			p.setIndentationLeft(420f);			
			document.add(p);
			
			p = new Paragraph("RAPORT WYMIANY WODOMIERZA", bold);
			p.setAlignment(Element.ALIGN_CENTER);
			p.setSpacingBefore(60f);
			document.add(p);
			
			document.add(table(form));
			
			p = new Paragraph(form.getInformations(), normal);
			p.setAlignment(Element.ALIGN_LEFT);
			p.setIndentationLeft(50f);
			p.setSpacingBefore(20f);
			document.add(p);
			
			document.add(signTable(form, sgn, workers));
			
			document.add(logo);
			document.add(qr);
			
			if(photos != null && photos.size() > 0){
				document.newPage();
				for(int i = 0 ; i < photos.size(); i++){
					try{
						if(photos.get(i) != null && !photos.get(i).equals("")){
							Log.d(TAG, photos.get(i));
							Bitmap temp = PictureHelper.bitmapFromUrl(photos.get(i));
							outstream = new ByteArrayOutputStream();
							temp.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
							Image photo = Image.getInstance(outstream.toByteArray());					
							photo.scaleToFit(document.getPageSize().getWidth()-50, document.getPageSize().getHeight());					
							document.add(photo);
						}
					}catch(Exception e){
						
					}
				}
			}	
			document.close();
			writer.close();
			return 1;
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
			return 0;
		} catch (DocumentException e) {
			Log.e(TAG, e.getMessage());
			return 0;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return 0;
		}catch(Exception e){
			Log.e(TAG, e.getMessage());
			return 0;
		}
	}
	
	private PdfPTable table(Form form){
		int cellPadding = 5;		
		PdfPTable table = new PdfPTable(8);
		table.setSpacingBefore(20);
		
		try {
			table.setWidths(new int[]{100, 120, 30, 60, 60, 50, 50, 50});
		} catch (DocumentException e) {
			Log.e(TAG, "Width error:" +e.getMessage());
		}			
		
		PdfPCell cell = new PdfPCell(new Paragraph("Adres: " + form.getAddress(), normal));			
		cell.setColspan(8);
		cell.setPadding(cellPadding);
		cell.setBorder(Rectangle.LEFT|Rectangle.RIGHT|Rectangle.TOP);
		cell.setBorderColorBottom(BaseColor.BLACK);
		table.addCell(cell);
		
		cell = new PdfPCell(new Paragraph("Odbiorca: " + form.getOwner(), normal));			
		cell.setColspan(8);			
		cell.setPadding(cellPadding);
		cell.setBorder(Rectangle.LEFT|Rectangle.RIGHT);
		cell.setBorderColorBottom(BaseColor.BLACK);
		table.addCell(cell);
		
		cell = new PdfPCell(new Paragraph("Data: " + form.getDate(), normal));			
		cell.setColspan(3);			
		cell.setPadding(cellPadding);
		cell.setBorder(Rectangle.LEFT|Rectangle.BOTTOM);
		cell.setBorderColorBottom(BaseColor.BLACK);
		table.addCell(cell);
		
		cell = new PdfPCell(new Paragraph("tel: " + form.getPhone(), normal));			
		cell.setColspan(5);			
		cell.setPadding(cellPadding);
		cell.setBorder(Rectangle.RIGHT|Rectangle.BOTTOM);
		cell.setBorderColorBottom(BaseColor.BLACK);
		table.addCell(cell);
		
		//Header
		Paragraph p = new Paragraph("WODOMIERZ", bold);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setNoWrap(true);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		p = new Paragraph("Nr fabryczny", bold);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		p = new Paragraph("DN", bold);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		p = new Paragraph("Typ\r\nProducent", bold);	
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		p = new Paragraph("Stan w (m3)", bold);	
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		p = new Paragraph("Rok\r\nlegalizacji", bold);		
		cell = new PdfPCell(p);;
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		p = new Paragraph("Czy plomba\r\nlegalizacyjna jest\r\nuszkodzona?", bold);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setColspan(2);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		//First row
		cell = new PdfPCell(new Paragraph("Zamontowany", bold));
		cell.setNoWrap(true);
		cell.setPadding(cellPadding);
		table.addCell(cell);
		
		p = new Paragraph(form.getNewSN(), normal);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		p = new Paragraph(form.getNewDN(), normal);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		p = new Paragraph(form.getNewType()+"\r\n"+form.getNewManufacturer(), normal);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		p = new Paragraph(form.getNewState(), normal);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		p = new Paragraph(form.getNewYear(), normal);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		if(form.getNewSeal().equals("Tak"))	p = new Paragraph("tak", normal);
		else p = new Paragraph("tak", strike);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		if(form.getNewSeal().equals("Nie"))	p = new Paragraph("nie", normal);
		else  p = new Paragraph("nie", strike);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		//Second row
		cell = new PdfPCell(new Paragraph("Wymontowany", bold));
		cell.setNoWrap(true);
		cell.setPadding(cellPadding);
		table.addCell(cell);
		
		p = new Paragraph(form.getOldSN(), normal);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		p = new Paragraph(form.getOldDN(), normal);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		p = new Paragraph(form.getOldType()+"\r\n"+form.getOldManufacturer(), normal);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		p = new Paragraph(form.getOldState(), normal);		
		cell = new PdfPCell(p);	
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		p = new Paragraph(form.getOldYear(), normal);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		if(form.getOldSeal().equals("Tak"))	p = new Paragraph("tak", normal);
		else p = new Paragraph("tak", strike);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		if(form.getOldSeal().equals("Nie"))	p = new Paragraph("nie", normal);
		else p = new Paragraph("nie", strike);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		//Third row
		cell = new PdfPCell(new Paragraph("Plomba nr 1", bold));
		cell.setPadding(cellPadding);
		table.addCell(cell);
		
		p = new Paragraph(form.getSealFirst(), normal);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setColspan(2);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		cell = new PdfPCell(new Paragraph("Przyczyna wymiany: "+form.getReason(), normal));
		cell.setPadding(cellPadding);
		cell.setColspan(5);
		table.addCell(cell);
		
		//Fourth row
		cell = new PdfPCell(new Paragraph("Plomba nr 2", bold));	
		cell.setPadding(cellPadding);
		table.addCell(cell);
		
		p = new Paragraph(form.getSealSecond(), normal);		
		cell = new PdfPCell(p);
		cell.setPadding(cellPadding);
		cell.setColspan(2);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
				
		cell = new PdfPCell(new Paragraph("Umiejscowienie wodomierza: "+form.getPlacement(), normal));
		cell.setPadding(cellPadding);
		cell.setColspan(5);		
		table.addCell(cell);
		
		//Fourth row
		cell = new PdfPCell(new Paragraph("Nr modu³u\r\nradiowego", bold));
		cell.setPadding(cellPadding);
		table.addCell(cell);
		
		cell = new PdfPCell(new Paragraph(form.getNewModuleNumber(), normal));
		cell.setPadding(cellPadding);
		cell.setColspan(7);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		table.setWidthPercentage(100);		
		return table;
	}
	
	private PdfPTable signTable(Form form, Image sgn, ArrayList<String> workers){
		
		int cellPadding = 5;		
		PdfPTable table = new PdfPTable(2);
		table.setSpacingBefore(15);			
		
		PdfPCell cell = new PdfPCell(new Paragraph("Data i podpis klienta: ", normal));				
		cell.setPadding(cellPadding);
		cell.setBorder(0);
		table.addCell(cell);
		
		cell = new PdfPCell(new Paragraph("Wykonali pracownicy: ", normal));				
		cell.setPadding(cellPadding);
		cell.setBorder(0);
		table.addCell(cell);
		
		cell = new PdfPCell(sgn);
		cell.setRowspan(workers.size());
		cell.setPadding(cellPadding);
		cell.setBorder(0);
		table.addCell(cell);
		
		for(int i = 0; i < workers.size(); i++){			
			cell = new PdfPCell(new Paragraph(workers.get(i), normal));				
			cell.setPadding(cellPadding);
			cell.setBorder(0);
			table.addCell(cell);	
		}	
		
		table.setWidthPercentage(90);	
		
		return table;
	}
	
}
