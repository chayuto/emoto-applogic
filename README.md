eMotoApp Logic Backend:

eMotoUtility Class:
    
    public static eMotoLoginResponse performLogin (String username, String password); //Login in with server: 

eMotoLoginResponse Class: 

    //Properties
    public boolean success;
    public String token;
    public String idle;
    public String username

eMotoAdsCollection Class: 

    //properties 
    public eMotoCell myEMotoCell;
    public String token;
    public Map<String,eMotoAds> map =  new HashMap<String,eMotoAds>();

    //methods
    public void getAdsCollection () //fill in Hashmap with eMotoAds object from server, Hashmap key is AdsIds
    public boolean approveAdsID(String adsID);


eMotoAds Class:
   
    //methods
    public String id();
    public String scheduleAssetId();
    public String getAdsImageURL();
    public String getAdsThumbnailURL();


Last modified 9:27pm 12/01/15


Change Logs:
