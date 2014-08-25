package com.pkumap.manager;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;

public class AudioManager implements OnInitListener {
	public  TextToSpeech mSpeech;
	private Context context;
	private static AudioManager singleton;
	
	public static AudioManager getInstance(Context ctx){
		if(singleton==null){
			singleton=new AudioManager(ctx);
		}
		return singleton;
	}
	public AudioManager(Context context){
		
		mSpeech=new TextToSpeech(context, this);
		this.context=context;
	}
	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		if(status==TextToSpeech.SUCCESS){
			int result=mSpeech.setLanguage(Locale.CHINA);
			if(result==TextToSpeech.LANG_MISSING_DATA||
					result==TextToSpeech.LANG_NOT_SUPPORTED){
				Toast.makeText(context, "语音不支持", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(context, "语音支持", Toast.LENGTH_SHORT).show();
			}
		}
	}
	/**
	 * 关闭语音功能
	 */
	public void close(){
		if(mSpeech!=null){
			mSpeech.stop();
			mSpeech.shutdown();
		}
	}

}
