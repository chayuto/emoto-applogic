#################################
#
# eMotoApp Logic Backend
#
#################################

Note:
Network - method should not be call in main Thread, use ASync Task or launch new Thread
Background - method spawn new background worker thread, method should not be called multiple times

##eMotoLogic Class:

    //constructor
    public eMotoLogic(Context mContext)

    //methods
    public void startAutoReauthenticate (eMotoLoginResponse mLoginResponse); //<Background> <Testing> should be called in main activity
    public void stopAutoReauthenticate ();

    public void startLocationService();
    public void stopLocationService();


##eMotoUtility Class:
    
    public static eMotoLoginResponse performLogin (String username, String password); //<Network> Login in with server:
    public static void performLoginWithLoginResponse(eMotoLoginResponse mLoginResponse); //<Network>

    public static void bypassSSLAllCertificate();

    public static JSONArray getCountryDataFromServer ();
    public static JSONArray getCityDataFromServer (String countryIDorShortName);
    public static JSONArray getZoneDataFromServer (String cityId);

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


##eMotoAds Class:

    //Constructor
    public eMotoAds(JSONObject ads);

    //methods
    public String id();
    public String scheduleAssetId();
    public String isApprovedStr();
    public String scheduleAssetId()
    public String getAdsImageURL();
    public String getAdsThumbnailURL();

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
    public void clearAssetLibrary()

