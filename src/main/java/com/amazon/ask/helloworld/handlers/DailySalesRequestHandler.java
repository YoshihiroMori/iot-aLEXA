package com.amazon.ask.helloworld.handlers;

import static com.amazon.ask.request.Predicates.*;

import java.util.List;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.helloworld.entities.SalesSpeechData;
import com.amazon.ask.model.DialogState;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.slu.entityresolution.Resolution;
import com.amazon.ask.model.slu.entityresolution.StatusCode;
import com.amazon.ask.model.slu.entityresolution.ValueWrapper;

public class DailySalesRequestHandler implements RequestHandler {

//	private static Logger logger = LogManager.getLogger(DailySalesRequestHandler.class);
	@Override
	public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("dalySales").and(requestType(IntentRequest.class)));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
		if ( intentRequest.getDialogState() != DialogState.COMPLETED ) {
			return input.getResponseBuilder().addDelegateDirective(null).build();
		} else {
			//System.err.println(intentRequest.toString());

			Slot slot =  intentRequest.getIntent().getSlots().get("section");
			String value = slot.getValue();

			List<Resolution> resolutions =  slot.getResolutions().getResolutionsPerAuthority();
			Resolution resolution =  resolutions.get(0);

			if(resolution.getStatus().getCode() == StatusCode.ER_SUCCESS_NO_MATCH) {
				//有効な選択なし
				return input.getResponseBuilder()
						.withSpeech(value+"という部門は判りません")
						.build();
			}


			ValueWrapper vw= resolution.getValues().get(0);
			String name = vw.getValue().getName();
			String id = vw.getValue().getId();

			System.err.println("return answer name=["+name+":"+id+"]");

			SalesSpeechData speechData = getData(id,name);

			return input.getResponseBuilder()
					.withSpeech(speechData.getSpeech())
					.withSimpleCard(speechData.getCard(), speechData.getText())
					.withReprompt(speechData.getText())
					.build();
		}
	}

	private SalesSpeechData getData(String id,String name) {

		SalesSpeechData salesSpeechData= new SalesSpeechData();

		salesSpeechData.setCard(name+"売上");
		salesSpeechData.setSpeech(name+"の１２月累計売上は、"
				+"<s>総売上は ￥8,026,260,000</s>"
				+"<s>レンタル売上は ￥5,330,459,000</s>"
				);
		salesSpeechData.setText(name+"の１２月累計売上は\n"
				+"総売上は ￥8,026,260,000 \n"
				+"  レンタル売上は ￥5,330,459,000 \n"
				);

		return salesSpeechData;


	}

}
