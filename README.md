#################################
#
# eMotoApp Logic Backend
#
#################################


eMotoUtility Class:
    
    public static eMotoLoginResponse performLogin (String username, String password); //<Network> Login in with server:
    public static void performLoginWithLoginResponse(eMotoLoginResponse mLoginResponse); //<Network>
    public static void bypassSSLAllCertificate();

eMotoLoginResponse Class <implements parcelable>:

    //Properties
    public boolean success;
    public String token;
    public String idle;
    public String username;

eMotoAdsCollection Class: 

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


eMotoAds Class:

    //property
    public JSONObject adsJSONObject; //to be considered for change to primitive class

    //methods
    public String id();
    public String scheduleAssetId();
    public String isApprovedStr();
    public String scheduleAssetId()
    public String getAdsImageURL();
    public String getAdsThumbnailURL();

eMotoCell Class:

    //property
    public String deviceID;
    public String deviceLatitude;
    public String deviceLongitude;

    //method
    public void putDeviceOnServer (String token);//<Network>


Change Logs:
