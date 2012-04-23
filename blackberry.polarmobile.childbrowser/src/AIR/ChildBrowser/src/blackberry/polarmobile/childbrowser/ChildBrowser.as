/**
 * Author: Shane Jonas
 * Child Browser Implementation for the PlayBook
 */

package blackberry.polarmobile.childbrowser
{
    // flash
    import flash.geom.Rectangle;
    import flash.display.Stage;
    import flash.events.MouseEvent;
    import flash.events.Event;
    import qnx.events.ExtendedLocationChangeEvent;
    import flash.display.Sprite;
    import flash.events.StageOrientationEvent;
    import flash.utils.setTimeout;
	
	//photo upload
	import json.JSON;
	import flash.utils.ByteArray;
	import flash.utils.Endian;
	import flash.events.*;
	import flash.net.*;
	
    import caurina.transitions.Tweener;

    // qnx 
    import qnx.media.QNXStageWebView;
    import qnx.ui.buttons.IconButton;
    import qnx.ui.skins.buttons.OutlineButtonSkinBlack;

    // webworks
    import webworks.extension.DefaultExtension;

    import flash.events.LocationChangeEvent;

    public class ChildBrowser extends DefaultExtension
    {
        private var childWebView:QNXStageWebView = null;
        private var closeButton:IconButton;
        private var refreshButton:IconButton;
        private var bgshape:Sprite;
        private var loading_bg_shape:Sprite;
        private var browserHeight;
        private var isVisible:Boolean = false;
        private var webViewUI:Sprite;
        private var jsEventHandler:String;
		
		//file upload globals
		private var sourceFile:String;
		private var postUrl:String;
		private var options:Object;
		private var httpStatus:int;


        //icons
        [Embed(source="assets/close.png")] 
        public static var Close:Class;
        [Embed(source="assets/refresh.png")] 
        public static var Refresh:Class;
        [Embed(source="assets/ajax-spinner-black-bg.gif")] 
        public static var Spinner:Class;

        public function ChildBrowser() 
        {
            super();
            this.isVisible = false
        }

        override public function getFeatureList():Array 
        {
            return new Array ("blackberry.polarmobile.childbrowser");
        }

        private function initBG()
        {
            if (this.isVisible)
            {
                return;
            }

            var self = this;
            webViewUI = new Sprite();
            bgshape = new Sprite();
            bgshape.graphics.beginFill(0x323232);
            bgshape.graphics.drawRect(0,0,webView.stage.stageWidth, webView.stage.stageHeight);
            webViewUI.addChildAt(bgshape, 0);

            //build buttons
            this.initUI();

            webViewUI.y = webView.stage.stageHeight;
            webView.stage.addChild(webViewUI);

            self.isVisible = true;

            function loaded(){
              setTimeout(function(){
                childWebView.stage = webView.stage;
                childWebView.zOrder = 1;
              }, 1000);
            }

            Tweener.addTween(webViewUI, {
              y: 0,
              time: 1,
              transition: 'easeOutExpo',
              onComplete: loaded
            });
        }

        public function clearCookies()
        {
          //if we dont have a webview, make one and put it in the background
          this.createBrowser()
          //clear the webviews cookies
          childWebView.clearCookies();
          childWebView.stage = null;
          //childWebView.dispose();
          //childWebView = null;
        }

        private function createBrowser()
        {
          if (childWebView == null) 
          {
              childWebView = new QNXStageWebView("ChildBrowser");
			  childWebView.userAgent = "Mozilla/5.0 (Linux; U; Android 2.2; en-us; SCH-I800 Build/FROYO) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
			  
              childWebView.stage = webView.stage;
              childWebView.viewPort = new Rectangle(0,50,webView.stage.stageWidth,browserHeight);
              childWebView.zOrder = -1;
              // events
              webView.stage.addEventListener(StageOrientationEvent.ORIENTATION_CHANGE, onOrientationChange);
          }
        }


        public function loadURL(url:String, myEventHandler:String)
        {
            var self = this;
            jsEventHandler = myEventHandler;
            browserHeight = webView.stage.stageHeight - 50;
            //if we dont have a webview, make one and put it in the background
            this.createBrowser();
            //put webview behind stage
            webView.zOrder = -1;
            //load this url
            childWebView.loadURL(url);
            this.initBG();

            childWebView.addEventListener(LocationChangeEvent.LOCATION_CHANGE,onLocationChanged);
           
        }

        private function onLocationChanged(event:ExtendedLocationChangeEvent)
        {
            var array:Array = new Array();
            array[0] = childWebView.location;
            evalJavaScriptEvent(jsEventHandler, array);
        }

        private function onOrientationChange(event:StageOrientationEvent)
        {
           var self = this
           this.removeUI();
           this.initBG()
           childWebView.viewPort = new Rectangle(0,50,bgshape.width,bgshape.height - 50);
        }

        public function getLocation():String
        {
            return childWebView.location;
        }

        public function forward()
        {
            childWebView.historyForward();
        }

        public function back()
        {
            childWebView.historyBack();
        }

        public function refresh()
        {
            childWebView.reload();
        }

        public function close()
        {
          var array:Array = new Array();
          array[0] = 1;
          array[1] = childWebView.location;
          childWebView.stage = null;
          childWebView.dispose();
          childWebView = null;


          Tweener.addTween(webViewUI, {
            y: webView.stage.stageHeight,
            delay: 0.5,
            time: 1,
            transition: 'easeOutExpo',
            onComplete: closeUI
          });
          evalJavaScriptEvent(jsEventHandler,array);
        }

        private function closeUI()
        {
          // the `dispose` method does not work when running inside of webworks,
          // as it closes then main `webView` instance. as a temp. work-around,
          // we hide the child
          this.removeUI();
          webView.stage.removeChild(webViewUI);
          webView.zOrder = 1;
        }

        public function closeCLICK(e:MouseEvent)
        {
          this.close();
        }

        public function refreshCLICK(e:MouseEvent)
        {
          this.refresh();
        }

        private function removeUI()
        {
          removeChild(bgshape);
          removeChild(closeButton);
          removeChild(refreshButton)
          this.isVisible = false;
        }

        //close button
        private function addClose()
        {
          closeButton = new IconButton();
          closeButton.setIcon(new Close());

          closeButton.setSize(266, 50);
          closeButton.setPosition(-5, 0);
            
          closeButton.setSkin(new OutlineButtonSkinBlack());
          closeButton.addEventListener(MouseEvent.CLICK, closeCLICK);
          addChild(closeButton);
        }

        //refresh button
        private function addRefresh()
        {
          refreshButton = new IconButton();
          refreshButton.setIcon(new Refresh());
          refreshButton.setSize(266, 50);
          refreshButton.setPosition(256, 0);
          refreshButton.setSkin(new OutlineButtonSkinBlack())
          refreshButton.addEventListener(MouseEvent.CLICK, refreshCLICK);
          addChild(refreshButton);
        }

        // UI Buttons
        private function initUI()
        {
          this.addClose();
          this.addRefresh();
        }

        public function getVisible():Boolean
        {
          return this.isVisible; 
        }

        // our own addChild implementation
        // maps back to stage of WebWorkAppTemplate.as
        private function addChild(obj)
        {
          webViewUI.addChild(obj);
          //always set added obj's to top
          //webViewUI.setChildIndex(obj, webView.stage.numChildren -1);
        }

        private function removeChild(obj)
        {
          webViewUI.removeChild(obj);
        }
		
		/* Photo Upload */
		private function jsLog(log:String) : void {
            var array:Array = new Array();
            array[0] = "LOG_ " + log;
            evalJavaScriptEvent(jsEventHandler, array);
		}
		
		public function uploadFile(mySourceFile:String, myPostUrl:String, myEventHandler:String, myoptions:String = null) : void {
			trace("Upload File: " + sourceFile);
			
			sourceFile = mySourceFile;
			postUrl = myPostUrl;
			jsEventHandler = myEventHandler;
			
			options = ( myoptions != "" && myoptions != null ) ? (JSON.decode(myoptions) as Object) : (new Object());
		
			var loader = new URLLoader(new URLRequest(sourceFile));
			loader.dataFormat = URLLoaderDataFormat.BINARY; 
			loader.addEventListener(Event.COMPLETE, loadedCompleteHandler);
			
			jsLog(postUrl);
		}
		
		private function handleHttpStatus(event : Event) : void {
			httpStatus = HTTPStatusEvent(event).status;
			jsLog( event.toString() );
		}

		private function handleComplete(event : Event) : void {
            var array:Array = new Array();
            array[0] = "SUCCESS " + httpStatus;
            evalJavaScriptEvent(jsEventHandler, array);
		}

		private function onError(event : IOErrorEvent) : void {
            var array:Array = new Array();
            array[0] = "FAIL " + event.toString();
            evalJavaScriptEvent(jsEventHandler, array);
		}

		private function onSecurityError(event : SecurityErrorEvent) : void {
            var array:Array = new Array();
            array[0] = "SECURITY FAIL";
            evalJavaScriptEvent(jsEventHandler, array);
		}
		
		
		/* File System */
		private function loadedCompleteHandler(e:Event):void
		{
			try
			{
				jsLog("loadedCompleteHandler " + e.target.dataFormat + " " + e.target.data.length);
				var urlRequest:URLRequest = new URLRequest();
				urlRequest.url = postUrl;
				urlRequest.contentType = 'multipart/form-data; boundary=' + getBoundary();
				urlRequest.method = URLRequestMethod.POST;
				urlRequest.data = getPostData(options.fileName, e.target.data, options.fileKey, options.mimeType, options.params);
				urlRequest.requestHeaders.push( new URLRequestHeader( 'Cache-Control', 'no-cache' ) );
			
				var urlLoader:URLLoader = new URLLoader();
				urlLoader.dataFormat = URLLoaderDataFormat.BINARY;
				urlLoader.addEventListener(HTTPStatusEvent.HTTP_RESPONSE_STATUS, handleHttpStatus);
				urlLoader.addEventListener(Event.COMPLETE, handleComplete);
				urlLoader.addEventListener(IOErrorEvent.IO_ERROR, onError);
				urlLoader.addEventListener(SecurityErrorEvent.SECURITY_ERROR, onSecurityError);
				urlLoader.load(urlRequest);
				jsLog("loadedCompleteHandler done");
			}
			catch (error:Error) 
            { 
				var array:Array = new Array();
				array[0] = "Unable to read file: " + error;
				evalJavaScriptEvent(jsEventHandler, array);
            }
		}
		
			
		/**
		 * Boundary used to break up different parts of the http POST body
		 */
		private static var _boundary:String = "";

		/**
		 * Get the boundary for the post.
		 * Must be passed as part of the contentType of the UrlRequest
		 */
		public static function getBoundary():String {

			if(_boundary.length == 0) {
				for (var i:int = 0; i < 0x20; i++ ) {
					_boundary += String.fromCharCode( int( 97 + Math.random() * 25 ) );
				}
			}
			return _boundary;
		}

		/**
		 * Create post data to send in a UrlRequest
		 */
		public function getPostData(fileName:String, byteArray:ByteArray, uploadDataFieldName:String = "file", mime:String = "application/octet-stream", parameters:Object = null):ByteArray {
			var i: int;
			var bytes:String;
			
			jsLog("getPostData");

			var postData:ByteArray = new ByteArray();
			postData.endian = Endian.BIG_ENDIAN;

			if(parameters == null) {
				parameters = new Object();
			}
			
			//add optional params
			for(var name:String in parameters) {
				jsLog("postvar: " + name);
				postData = BOUNDARY(postData);
				postData = LINEBREAK(postData);
				bytes = 'Content-Disposition: form-data; name="' + name + '"';
				for ( i = 0; i < bytes.length; i++ ) {
					postData.writeByte( bytes.charCodeAt(i) );
				}
				postData = LINEBREAK(postData);
				postData = LINEBREAK(postData);
				postData.writeUTFBytes(parameters[name]);
				postData = LINEBREAK(postData);
			}
	
			//add Filedata to postData
			postData = BOUNDARY(postData);
			postData = LINEBREAK(postData);
			bytes = 'Content-Disposition: form-data; name="'+uploadDataFieldName+'"; filename="';
			for ( i = 0; i < bytes.length; i++ ) {
				postData.writeByte( bytes.charCodeAt(i) );
			}
			postData.writeUTFBytes(fileName);
			postData = QUOTATIONMARK(postData);
			postData = LINEBREAK(postData);
			for ( i = 0; i < mime.length; i++ ) {
				postData.writeByte( mime.charCodeAt(i) );
			}
			
			jsLog( String(postData) );
			
			postData = LINEBREAK(postData);
			postData = LINEBREAK(postData);
			postData.writeBytes(byteArray, 0, byteArray.length);
			postData = LINEBREAK(postData);

			//add form button code
			postData = LINEBREAK(postData);
			postData = BOUNDARY(postData);
			postData = LINEBREAK(postData);
			bytes = 'Content-Disposition: form-data; name="Upload"';
			for ( i = 0; i < bytes.length; i++ ) {
				postData.writeByte( bytes.charCodeAt(i) );
			}
			postData = LINEBREAK(postData);
			postData = LINEBREAK(postData);
			bytes = 'Submit';
			for ( i = 0; i < bytes.length; i++ ) {
				postData.writeByte( bytes.charCodeAt(i) );
			}
			postData = LINEBREAK(postData);


			//closing boundary
			postData = BOUNDARY(postData);
			postData = DOUBLEDASH(postData);
			
			jsLog("getPostData done");

			return postData;
		}

		/**
		 * Add a boundary to the PostData with leading doubledash
		 */
		private static function BOUNDARY(p:ByteArray):ByteArray {
			var l:int = getBoundary().length;

			p = DOUBLEDASH(p);
			for (var i:int = 0; i < l; i++ ) {
				p.writeByte( _boundary.charCodeAt( i ) );
			}
			return p;
		}

		/**
		 * Add one linebreak
		 */
		private static function LINEBREAK(p:ByteArray):ByteArray {
			p.writeShort(0x0d0a);
			return p;
		}

		/**
		 * Add quotation mark
		 */
		private static function QUOTATIONMARK(p:ByteArray):ByteArray {
			p.writeByte(0x22);
			return p;
		}

		/**
		 * Add Double Dash
		 */
		private static function DOUBLEDASH(p:ByteArray):ByteArray {
			p.writeShort(0x2d2d);
			return p;
		}



    }
}
