#################################
#
# eMotoApp Logic Backend
#
#################################

# 1. Sections

##Ads Managements Classes:
eMotoAds, eMotoAdsCollection, eMotoAssetLibrary


##eMoto Background Service:
###Background Service handles all background Processes
  * keeping track of the eMotoCell device and upload to server
  * update authentication token in background
  * handle bluetooth communications (to be Implemented)

###Implementation
  1. AndroidManifest.xml

  ``` xml
  <service
   android:name="me.chayut.eMotoLogic.eMotoService"
   android:label="eMotoService">
  </service>
  ```

####Usage Note:
  * eMotoService is running in background by default throughout application lifetime
  * Activities call call service API via using Intent with commands.
  * some command will require to take Extras as input perimeters
  * Activities can listen to the response from the eMotoService via implimenting BroadcaseListener

# 2. API Calls
####Note:
  * Network - method should not be call in main Thread, use ASync Task or launch new Thread
  * Background - method spawn new background worker thread, method should not be called multiple times


##eMotoService Class:

    // Defines Intent action
    public static final String BROADCAST_ACTION = "com.emotovate.android.eMotoApp.BROADCAST";
    public static final String BROADCAST_STATUS = "com.emotovate.android.eMotoApp.STATUS";

    //Public RESPONSE
    public static final String RES_LOCATION_UPDATE = "RES_LOCATION_UPDATE";
    public static final String RES_LOCATION_ERROR = "RES_LOCATION_ERROR";
    public static final String RES_TOKEN_UPDATE = "RES_TOKEN_UPDATE";
    public static final String RES_TOKEN_UNAUTHORIZED = "RES_TOKEN_UNAUTHORIZED";
    public static final String RES_EXCEPTION_ENCOUNTERED = "RES_EXCEPTION_ENCOUNTERED";

    //Public CMD
    public final static String CMD_STARTAUTOREAUTHENTICATE = "CMD_STARTAUTOREAUTHENTICATE";
    public final static String CMD_GETTOKEN = "CMD_GETTOKEN";
    public final static String CMD_STARTLOCATIONSERVICE = "CMD_STARTLOCATIONSERVICE";
    public final static String CMD_STOPLOCATIONSERVICE = "CMD_STOPLOCATIONSERVICE";

##eMotoUtility Class:

    ```java
    public static eMotoLoginResponse performLogin (String username, String password); //<Network> Login in with server:
    public static void performLoginWithLoginResponse(eMotoLoginResponse mLoginResponse); //<Network>

    public static void bypassSSLAllCertificate();

    public static JSONArray getCountryDataFromServer (); //<Network>
    public static JSONArray getCityDataFromServer (String countryIDorShortName); //<Network>
    public static JSONArray getZoneDataFromServer (String cityId); //<Network>
    ```

##eMotoLoginResponse Class <implements parcelable>:

    //Properties
    public boolean success;
    public String token;
    public String idle;
    public String username;
    public String credential; //temporary, unsafe

##eMotoAdsCollection Class:

    //properties 
    public eMotoCell eMotoCell;
    public String token;
    public Map<String,eMotoAds> adsHashMap =  new HashMap<String,eMotoAds>();

    //methods
    public eMotoAds getAdsWithId (String id);
    public boolean removeAdsWithId (String id);
    public void getAdsCollection (String token)  //<Network>
    public boolean approveAdsWithID(String adsID,String token); //<Network>
    public boolean unapproveAdsWithID(String adsID,String token); //<Network>


##eMotoAds Class <implements Parcelable>

    //Constructor
    public eMotoAds(JSONObject ads);

    //methods
    public String id();
    public String scheduleAssetId();
    public String isApprovedStr();
    public String scheduleAssetId()
    public String getAdsImageURL();
    public String getAdsThumbnailURL();
    public String getAdsExtension();
    public int getAdsWidth();
    public int getAdsHeight();
    public int getAdsSize()

##eMotoCell Class:

    //property
    public String deviceID;
    public String deviceLatitude;
    public String deviceLongitude;

    //method
    public void putDeviceOnServer (String token);//<Network>


##eMotoAssetLibrary Class:
 Asset Library handle download of the images and do internal caching.

    //method
    public Bitmap getThumbnail (eMotoAds ads); //<Network>
    public Bitmap getThumbnail (String AdsID, String strAdsThumbnailURL); //<Network>
    public void clearAssetLibrary()

