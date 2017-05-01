package ionPrediction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PredictorMain {

	// Generic output
	private static String outputDir = "C:\\Users\\dpolasky\\Desktop\\DataAnalysis\\_PeptideAnalysis\\Theoretical Peptide lists";
//	private static String outfilename = "testPeptides";
	
	// Turn on/off various analysis types/possibilities
	private static boolean TMPAnalyze = true;
	private static boolean DMPAnalyze = false;
	
	private static boolean mTagAnalyze = false;
	private static boolean DC4Analyze = false;
	private static boolean BS3Analyze = false;
	private static boolean TMPPAnalyze = false;
	private static boolean GEEAnalyze = false;
	private static boolean GEE_H_Analyze = false;
	private static boolean GAAnalyze = false;
	private static boolean EDCAnalyze = false;
	
	
	private static boolean aIons = true;
	private static boolean byIons = true;
	private static boolean czIons = false;
	private static boolean vIons = false;
	private static boolean dIons = false;
	private static boolean xIons = false;
	private static boolean wIons = false;
	private static boolean internalIons = false;
	
	private static boolean lossAdducts = true;	
	private static boolean doubleLossAdducts = false;
	private static boolean miscAdducts = false;

	private static boolean newDecoys = true;	// -500 ppm for decoys instead of sequence reverse
	private static final double PPM_OFFSET = -500;
	
	// Max length of fragments to compute modifications for (reduce to speed up)
	private static final int[] CHARGES = {1,2,3,4,5};
//	private static final int[] INTERNAL_CHARGES = {1,2,3};
//	private static final int[] CHARGES = {1,2,3};
//	private static final int[] CHARGES = {1};
	private static final int MAX_FRAGMENT_LENGTH = 150;
	private static final int MIN_FRAGMENT_LENGTH = 1;
	
	// Max mz value to compute theoretical ions - set to match mz range being observed
	private static final double MZ_CUTOFF = 3000;
//	private static final double MZ_MIN = 100;
	private static final double MZ_MIN = 0;
	
	// Avidin
//	public static String protein = "ARKCSLTGKWTNDLGSNMTIGAVNSRGEFTGTYITAVTATSNEIKESPLHGTQNTINKRTQPTFGFTVNWKFSESTTVFTGQCFIDRNGKEVLKTMWLLRSSVNDIGDDWKATRVGINIFTRLRTQKE";
	// glycoform - full ("X" at position 17) - shouldn't stick around intact at high E
//	public static String protein = "ARKCSLTGKWTNDLGSXMTIGAVNSRGEFTGTYITAVTATSNEIKESPLHGTQNTINKRTQPTFGFTVNWKFSESTTVFTGQCFIDRNGKEVLKTMWLLRSSVNDIGDDWKATRVGINIFTRLRTQKE";
	//	private static String decoy = "EKQTRLRTFINIGVRTAKWDDGIDNVSSRLLWMTKLVEKGNRDIFCQGTFVTTSESFKWNVTFGFTPQTRKNITNQTGHLPSEKIENSTATVATIYTGTFEGRSNVAGITMNSGLDNTWKGTLSCKRASLGPAVLALSLLLLLLLPSTAHVM";
//	private static String outfilename = "Avidin";
	// Avi TMP
//	private static String outputDir = "C:\\Users\\dpolasky\\Desktop\\DataAnalysis\\_PeptideAnalysis\\Theoretical Peptide lists\\Avidin\\TMP";
//	private static String outputDir = "C:\\Users\\dpolasky\\Desktop\\DataAnalysis\\_PeptideAnalysis\\Theoretical Peptide lists\\Avidin";
	
	// Myoglobin (equine)
//	public static String protein = "GLSDGEWQQVLNVWGKVEADIAGHGQEVLIRLFTGHPETLEKFDKFKHLKTEAEMKASEDLKKHGTVVLTALGGILKKKGHHEAELKPLAQSHATKHKIPIKYLEFISDAIIHVLHSKHPGDFGADAQGAMTKALELFRNDIAAKYKELGFQG";
//	private static String outfilename = "Myo";

	// Aldolase (rabbit)
//	public static String protein = "PHSHPALTPEQKKELSDIAHRIVAPGKGILAADESTGSIAKRLQSIGTENTEENRRFYRQLLLTADDRVNPCIGGVILFHETLYQKADDGRPFPQVIKSKGGVVGIKVDKGVVPLAGTNGETTTQGLDGLSERCAQYKKDGADFAKWRCVLKIGEHTPSALAIMENANVLARYASICQQNGIVPIVEPEILPDGDHDLKRCQYVTEKVLAAVYKALSDHHIYLEGTLLKPNMVTPGHACTQKYSHEEIAMATVTALRRTVPPAVTGVTFLSGGQSEEEASINLNAINKCPLLKPWALTFSYGRALQASALKAWGGKKENLKAAQEEYVKRALANSLACQGKYTPSGQAGAAASESLFISNHAY";
//	private static String outfilename = "Aldol";
	
	// Carbonic Anhydrase (bovine)
//	public static String protein = "SHHWGYGKHNGPEHWHKDFPIANGERQSPVDIDTKAVVQDPALKPLALVYGEATSRRMVNNGHSFNVEYDDSQDKAVLKDGPLTGTYRLVQFHFHWGSSDDQGSEHTVDRKKYAAELHLVHWNTKYGDFGTAAQQPDGLAVVGVFLKVGDANPALQKVLDALDSIKTKGKSTDFPNFDPGSLLPNVLDYWTYPGSLTTPPLLESVTWIVLKEPISVSSQQMLKFRTLNFNAEGEPELLMLANWRPAQPLKNRQVRGFPK";
	// S-1 Acetylated version (fixed)
//	public static String protein = "ZHHWGYGKHNGPEHWHKDFPIANGERQSPVDIDTKAVVQDPALKPLALVYGEATSRRMVNNGHSFNVEYDDSQDKAVLKDGPLTGTYRLVQFHFHWGSSDDQGSEHTVDRKKYAAELHLVHWNTKYGDFGTAAQQPDGLAVVGVFLKVGDANPALQKVLDALDSIKTKGKSTDFPNFDPGSLLPNVLDYWTYPGSLTTPPLLESVTWIVLKEPISVSSQQMLKFRTLNFNAEGEPELLMLANWRPAQPLKNRQVRGFPK";
//	private static String outfilename = "CbAn-ptm";
	
	// BSA
//	public static String protein = "DTHKSEIAHRFKDLGEEHFKGLVLIAFSQYLQQCPFDEHVKLVNELTEFAKTCVADESHAGCEKSLHTLFGDELCKVASLRETYGDMADCCEKQEPERNECFLSHKDDSPDLPKLKPDPNTLCDEFKADEKKFWGKYLYEIARRHPYFYAPELLYYANKYNGVFQECCQAEDKGACLLPKIETMREKVLASSARQRLRCASIQKFGERALKAWSVARLSQKFPKAEFVEVTKLVTDLTKVHKECCHGDLLECADDRADLAKYICDNQDTISSKLKECCDKPLLEKSHCIAEVEKDAIPENLPPLTADFAEDKDVCKNYQEAKDAFLGSFLYEYSRRHPEYAVSVLLRLAKEYEATLEECCAKDDPHACYSTVFDKLKHLVDEPQNLIKQNCDQFEKLGEYGFQNALIVRYTRKVPQVSTPTLVEVSRSLGKVGTRCCTKPESERMPCTEDYLSLILNRLCVLHEKTPVSEKVTKCCTESLVNRRPCFSALTPDETYVPKAFDEKLFTFHADICTLPDTEKQIKKQTALVELLKHKPKATEEQLKTVMENFVAFVDKCCAADDKEACFAVEGPKLVVSTQTALA";
//	private static String outfilename = "BSA";
	
	// Catalase (bovine)
//	public static String protein = "ADNRDPASDQMKHWKEQRAAQKPDVLTTGGGNPVGDKLNSLTVGPRGPLLVQDVVFTDEMAHFDRERIPERVVHAKGAGAFGYFEVTHDITRYSKAKVFEHIGKRTPIAVRFSTVAGESGSADTVRDPRGFAVKFYTEDGNWDLVGNNTPIFFIRDALLFPSFIHSQKRNPQTHLKDPDMVWDFWSLRPESLHQVSFLFSDRGIPDGHRHMNGYGSHTFKLVNANGEAVYCKFHYKTDQGIKNLSVEDAARLAHEDPDYGLRDLFNAIATGNYPSWTLYIQVMTFSEAEIFPFNPFDLTKVWPHGDYPLIPVGKLVLNRNPVNYFAEVEQLAFDPSNMPPGIEPSPDKMLQGRLFAYPDTHRHRLGPNYLQIPVNCPYRARVANYQRDGPMCMMDNQGGAPNYYPNSFSAPEHQPSALEHRTHFSGDVQRFNSANDDNVTQVRTFYLKVLNEEQRKRLCENIAGHLKDAQLFIQKKAVKNFSDVHPEYGSRIQALLDKYNEEKPKNAVHTYVQHGSHLSAREKANL";
//	private static String outfilename = "Cat";
	
	// Concanavalin A (jack bean)
//	public static String protein = "ADTIVAVELDTYPNTDIGDPSYPHIGIDIKSVRSKKTAKWNMQNGKVGTAHIIYNSVDKRLSAVVSYPNADSATVSYDVDLDNVLPEWVRVGLSASTGLYKETNTILSWSFTSKLKSNSTHETNALHFMFNQFSKDQKDLILQGDATTGTDGNLELTRVSSNGSPQGSSVGRALFYAPVHIWESSAVVASFEATFTFLIKSPDSHPADGIAFFISNIDSSIPSGSTGRLLGLFPDAN";
//	private static String outfilename = "ConA";
	
	// Pyruvate kinase (rabbit)
//	public static String protein = "SKSHSEAGSAFIQTQQLHAAMADTFLEHMCRLDIDSAPITARNTGIICTIGPASRSVETLKEMIKSGMNVARMNFSHGTHEYHAETIKNVRTATESFASDPILYRPVAVALDTKGPEIRTGLIKGSGTAEVELKKGATLKITLDNAYMEKCDENILWLDYKNICKVVDVGSKVYVDDGLISLQVKQKGPDFLVTEVENGGFLGSKKGVNLPGAAVDLPAVSEKDIQDLKFGVEQDVDMVFASFIRKAADVHEVRKILGEKGKNIKIISKIENHEGVRRFDEILEASDGIMVARGDLGIEIPAEKVFLAQKMIIGRCNRAGKPVICATQMLESMIKKPRPTRAEGSDVANAVLDGADCIMLSGETAKGDYPLEAVRMQHLIAREAEAAMFHRKLFEELARSSSHSTDLMEAMAMGSVEASYKCLAAALIVLTESGRSAHQVARYRPRAPIIAVTRNHQTARQAHLYRGIFPVVCKDPVQEAWAEDVDLRVNLAMNVGKARGFFKKGDVVIVLTGWRPGSGFTNTMRVVPVP";
//	private static String outfilename = "PK";
	
	//ADH I (Saccharomyces cerevisiae) - S2 IS ACETYLATED! 
//	public static String protein = "SIPETQKGVIFYESHGKLEYKDIPVPKPKANELLINVKYSGVCHTDLHAWHGDWPLPVKLPLVGGHEGAGVVVGMGENVKGWKIGDYAGIKWLNGSCMACEYCELGNESNCPHADLSGYTHDGSFQQYATADAVQAAHIPQGTDLAQVAPILCAGITVYKALKSANLMAGHWVAISGAAGGLGSLAVQYAKAMGYRVLGIDGGEGKEELFRSIGGEVFIDFTKEKDIVGAVLKATDGGAHGVINVSVSEAAIEASTRYVRANGTTVLVGMPAGAKCCSDVFNQVVKSISIVGSYVGNRADTREALDFFARGLVKSPIKVVGLSTLPEIYEKMEKGQIVGRYVVDTSK";
//	public static String protein = "ZIPETQKGVIFYESHGKLEYKDIPVPKPKANELLINVKYSGVCHTDLHAWHGDWPLPVKLPLVGGHEGAGVVVGMGENVKGWKIGDYAGIKWLNGSCMACEYCELGNESNCPHADLSGYTHDGSFQQYATADAVQAAHIPQGTDLAQVAPILCAGITVYKALKSANLMAGHWVAISGAAGGLGSLAVQYAKAMGYRVLGIDGGEGKEELFRSIGGEVFIDFTKEKDIVGAVLKATDGGAHGVINVSVSEAAIEASTRYVRANGTTVLVGMPAGAKCCSDVFNQVVKSISIVGSYVGNRADTREALDFFARGLVKSPIKVVGLSTLPEIYEKMEKGQIVGRYVVDTSK";
//	private static String outfilename = "ADH";
	
	// B-galactosidase (Escherichia coli)
//	public static String protein = "TMITDSLAVVLQRRDWENPGVTQLNRLAAHPPFASWRNSEEARTDRPSQQLRSLNGEWRFAWFPAPEAVPESWLECDLPEADTVVVPSNWQMHGYDAPIYTNVTYPITVNPPFVPTENPTGCYSLTFNVDESWLQEGQTRIIFDGVNSAFHLWCNGRWVGYGQDSRLPSEFDLSAFLRAGENRLAVMVLRWSDGSYLEDQDMWRMSGIFRDVSLLHKPTTQISDFHVATRFNDDFSRAVLEAEVQMCGELRDYLRVTVSLWQGETQVASGTAPFGGEIIDERGGYADRVTLRLNVENPKLWSAEIPNLYRAVVELHTADGTLIEAEACDVGFREVRIENGLLLLNGKPLLIRGVNRHEHHPLHGQVMDEQTMVQDILLMKQNNFNAVRCSHYPNHPLWYTLCDRYGLYVVDEANIETHGMVPMNRLTDDPRWLPAMSERVTRMVQRDRNHPSVIIWSLGNESGHGANHDALYRWIKSVDPSRPVQYEGGGADTTATDIICPMYARVDEDQPFPAVPKWSIKKWLSLPGETRPLILCEYAHAMGNSLGGFAKYWQAFRQYPRLQGGFVWDWVDQSLIKYDENGNPWSAYGGDFGDTPNDRQFCMNGLVFADRTPHPALTEAKHQQQFFQFRLSGQTIEVTSEYLFRHSDNELLHWMVALDGKPLASGEVPLDVAPQGKQLIELPELPQPESAGQLWLTVRVVQPNATAWSEAGHISAWQQWRLAENLSVTLPAASHAIPHLTTSEMDFCIELGNKRWQFNRQSGFLSQMWIGDKKQLLTPLRDQFTRAPLDNDIGVSEATRIDPNAWVERWKAAGHYQAEAALLQCTADTLADAVLITTAHAWQHQGKTLFISRKTYRIDGSGQMAITVDVEVASDTPHPARIGLNCQLAQVAERVNWLGLGPQENYPDRLTAACFDRWDLPLSDMYTPYVFPSENGLRCGTRELNYGPHQWRGDFQFNISRYSQQQLMETSHRHLLHAEEGTWLNIDGFHMGIGGDDSWSPSVSAEFQLSAGRYHYQLVWCQK";
//	private static String outfilename = "Bgal";
	
	// Ovalbumin (chicken) - N-term (G) is acetylated! 
//	public static String protein = "GSIGAASMEFCFDVFKELKVHHANENIFYCPIAIMSALAMVYLGAKDSTRTQINKVVRFDKLPGFGDSIEAQCGTSVNVHSSLRDILNQITKPNDVYSFSLASRLYAEERYPILPEYLQCVKELYRGGLEPINFQTAADQARELINSWVESQTNGIIRNVLQPSSVDSQTAMVLVNAIVFKGLWEKAFKDEDTQAMPFRVTEQESKPVQMMYQIGLFRVASMASEKMKILELPFASGTMSMLVLLPDEVSGLEQLESIINFEKLTEWTSSNVMEERKIKVYLPRMKMEEKYNLTSVLMAMGITDVFSSSANLSGISSAESLKISQAVHAAHAEINEAGREVVGSAEAGVDAASVSEEFRADHPFLFCIKHIATNAVLFFGRCVSP";
//	public static String protein = "JSIGAASMEFCFDVFKELKVHHANENIFYCPIAIMSALAMVYLGAKDSTRTQINKVVRFDKLPGFGDSIEAQCGTSVNVHSSLRDILNQITKPNDVYSFSLASRLYAEERYPILPEYLQCVKELYRGGLEPINFQTAADQARELINSWVESQTNGIIRNVLQPSSVDSQTAMVLVNAIVFKGLWEKAFKDEDTQAMPFRVTEQESKPVQMMYQIGLFRVASMASEKMKILELPFASGTMSMLVLLPDEVSGLEQLESIINFEKLTEWTSSNVMEERKIKVYLPRMKMEEKYNLTSVLMAMGITDVFSSSANLSGISSAESLKISQAVHAAHAEINEAGREVVGSAEAGVDAASVSEEFRADHPFLFCIKHIATNAVLFFGRCVSP";
//	private static String outfilename = "Oval";
	
	
	// Cyt C
//	private static String protein = "MGDVEKGKKIFVQKCAQCHTVEKGGKHKTGPNLHGLFGRKTGQAPGFTYTDANKNKGITWKEETLMEYLENPKKYIPGTKMIFAGIKKKTEREDLIAYLKKATNE";
	
	// Ubq
	public static String protein = "MQIFVKTLTGKTITLEVEPSDTIENVKGKIQEKEGIPPDQQRLIFAGKQLEDGRTLSDYNIQKESTLHLVLRLRGG";
	private static String outfilename = "Ubq";
	//	private static String decoy = "GGRLRLVLHLTSEKQINYDSLTRGDELQKGAFILRQQDPPIGEKEQIKGKVNEITDSPEVELTITKGTLTKVFIQM";
	
	// Misc
//	public static String protein = "MQIF";
//	public static String protein = "SDREYPLLIR";
//	public static String protein = "SDREYPLLIRIITTVVR";
//	public static String protein = "SDREYPLLIRSDSDR";
//	public static String protein = "AKDA";

	private static ArrayList<Ion> adducts;
	private static ArrayList<Ion> amineLosses;
	private static ArrayList<Ion> waterLosses;
	private static ArrayList<Ion> lysMods;
	private static ArrayList<Ion> acidicMods;
	private static ArrayList<Ion> allMods;
	private ArrayList<String> iontypes; 
	
	// NEW AA masses - calculated from elemental formula at greater precision than the Mascot ref masses.
	// Elemental comps from mMass source internals (in mspy/blocks)
	private static double A = 71.0371137878;
	private static double R = 156.1011110281;
	private static double N = 114.0429274472;
	private static double D = 115.0269430320;
	private static double C = 103.0091844778;
	private static double E = 129.0425930962;
	private static double Q = 128.0585775114;
	private static double G = 57.0214637236;
	private static double H = 137.0589118624;
	private static double I = 113.0840639804;
	private static double L = 113.0840639804;
	private static double K = 128.0949630177;
	private static double M = 131.0404846062;
	private static double F = 147.0684139162;
	private static double P = 97.0527638520;
	private static double S = 87.0320284099;
	private static double T = 101.0476784741;
	private static double W = 186.0793129535;
	private static double Y = 163.0633285383;
	private static double V = 99.0684139162;
	
	private static double X = 1574.5717111996;		// Asp-17 glycosylated in Avidin with 4 GlcNAc, 4 Mannose for 1460.52878
	private static double Z = 129.042593088;		// Acetylated Serine (S + 42.0105646837 Da)
	private static double J = 99.0320284043;		// Acetylated glycine (at n-terminus)
	
	// Other useful masses
	private static final double Hydrogen = 1.0078250321;
	private static final double ProtonMass = 1.0072764522;
	private static final double Oxygen = 15.9949146196;
	private static final double Carbon = 12.000000000;
	private static final double Nitrogen = 14.0030740049;
//	private static final double electronMass = 0.00054858;
	private static final double CO_loss = -27.9949146196;	// Billy noticed losses of 28 (NB - loss of 28 matches to b -> a semi-internal cleavage)
	private static final double PEPTIDE_BACKBONE_MASS = 56.0136386886;
	
	// Adducts and xlinks
	private static final double H2O = -18.010565;	// Negative for neutral loss of water
	private static final double NH3 = -17.026549;	// Negative for neutral loss of ammonia
	private static final double NH3andH2O = -35.037114;
	private static final double H2Ox2 = -36.021130;
	private static final double NH3x2 = -34.053098;
	
	private static final double Na = 21.981945;		// Mass of H+ is subtracted from Na+ mass, since Na+ will replace it
	private static final double HEPES = 238.098730;	// Neutral monoisotopic mass of HEPES
	private static final double TEA = 161.1415788533;
	
//	private static double DC4xlink = 248.153648;
//	private static double BS3xlink = 138.07165;

	private static final double DC4_DE_Full = 266.1641397427;
//	private static final double DC4_DE_Full_1H = 266.1635911628;		// not sure if 2-arm version ion pairs or not (so subtract 1 H+ or 2)
	private static final double DC4_DE_1arm = 179.1195352738;
	private static final double DC4_DE_Cleaved = 68.0267633304;	
	private static final double MtagDeadEnd = 194.1430103701;
//	private static final double BS3DeadEnd = 156.0786442515;
	private static final double BS3DeadEnd = 155.0708192194;
//	private static final double BS3xlink = 137.0602545331;
//	private static final double BS3xlink = 138.0680795652;
	private static final double TMP = 104.0631488367;	// should be 104.0636002566?
	private static final double TMPP = 572.1721665181;	// should be 571.181133886?
	private static final double GEE = 85.0527638489;
	private static final double GEE_hydro = 57.0214637206;
	private static final double GA = 56.037448138;
	private static final double EDC = 155.1422475598;
	private static final double DMP = 120.05697;
	private static final double DMP_meth = 106.04132;
	
	// Descriptive Strings for outputting to file
	public static final String MtagDeadEndName = "mTag_full";
	public static final String mTagCleavedName = "mTag_cleaved";
	public static final String DC4_DE_CleavedName = "DC4_DE_cleaved";
	public static final String DC4_DE_1armName = "DC4-1arm";
	public static final String DC4_DE_FullName = "DC4-full";
	public static final String BS3_DE_Name = "BS3-full";
	public static final String BS3_xlink_Name = "BS3-xlink";
	public static final String TMP_Name = "TMP";
	public static final String TMPP_Name = "TMPP/TPAS";
	public static final String GEE_Name = "GEE";
	public static final String GEE_hydro_Name = "GEE-hydro";
	public static final String GA_Name = "GA";
	public static final String EDC_Name = "EDC";	
	public static final String DMP_Name = "DMP-pyr";
	public static final String DMP_Name_me = "DMP-meth";
	
	// AA target lists for various modifiers
	public ArrayList<String> PRIMARY_AMINE = new ArrayList<String>();
	public ArrayList<String> ACIDIC = new ArrayList<String>();
	public ArrayList<String> NONSPECIFIC = new ArrayList<String>();
	
	// OLD Amino acid masses by 1 letter code - from Mascot amino acid reference data, found at
		// http://www.matrixscience.com/help/aa_help.html
//		private static double A = 71.037114;
//		private static double R = 156.101111;
//		private static double N = 114.042927;
//		private static double D = 115.026943;
//		private static double C = 103.009185;
//		private static double E = 129.042593;
//		private static double Q = 128.058578;
//		private static double G = 57.021464;
//		private static double H = 137.058912;
//		private static double I = 113.084064;
//		private static double L = 113.084064;
//		private static double K = 128.094963;
//		private static double M = 131.040485;
//		private static double F = 147.068414;
//		private static double P = 97.052764;
//		private static double S = 87.032028;
//		private static double T = 101.047679;
//		private static double W = 186.079313;
//		private static double Y = 163.06332;
//		private static double V = 99.068414;
		
	public PredictorMain(){
		adducts = new ArrayList<Ion>();
		lysMods = new ArrayList<Ion>();	
		acidicMods = new ArrayList<Ion>();
		iontypes = new ArrayList<String>();
		allMods = new ArrayList<Ion>();
		
		amineLosses = new ArrayList<Ion>();
		waterLosses = new ArrayList<Ion>();
		
		// initialize AA target arrays
		PRIMARY_AMINE.add("K");
		ACIDIC.add("D");
		ACIDIC.add("E");	
		
		outfilename = outfilename + "_";
		if (aIons){
			iontypes.add("a");
			outfilename = outfilename + "a,";
		}
		if (byIons){
			iontypes.add("b");
			iontypes.add("y");
			outfilename = outfilename + "b,y,";
		}
		if (czIons){
			iontypes.add("c");
			iontypes.add("z");
			outfilename = outfilename + "c,z,";
		}
		if (dIons){
			iontypes.add("d");
			outfilename = outfilename + "d,";
		}
		if (vIons){
			iontypes.add("v");
			outfilename = outfilename + "v,";
		}
		if (xIons){
			iontypes.add("x");
			outfilename = outfilename + "x,";
		}
		if (wIons){
			iontypes.add("w");
			outfilename = outfilename + "w,";
		}
		if (internalIons){
			iontypes.add("int-a");
			iontypes.add("int-b");
			outfilename = outfilename + "int";
		}
		
		if (lossAdducts){
			waterLosses.add(new Ion(H2O,""," ","H2O loss", " ", NONSPECIFIC));
			amineLosses.add(new Ion(NH3,""," ","NH3 loss", " ", NONSPECIFIC));
			adducts.add(new Ion(NH3andH2O,""," ","H2O and NH3 loss", " ", NONSPECIFIC));
			outfilename = outfilename + "_los";
			if (doubleLossAdducts){
				waterLosses.add(new Ion(H2Ox2,""," ","2xH2O loss", " ", NONSPECIFIC));
				amineLosses.add(new Ion(NH3x2,""," ","2xNH3 loss", " ", NONSPECIFIC));
				outfilename = outfilename + "x2";
			}
		}
		if (miscAdducts){
			adducts.add(new Ion(Na,""," ","Na", " ", NONSPECIFIC));
			adducts.add(new Ion(TEA,""," ","TEA", " ", NONSPECIFIC));
			adducts.add(new Ion(HEPES,""," ","HEPES", " ", NONSPECIFIC));
			outfilename = outfilename + "_w-ad";
		}

		// mTag mods
		
		if (mTagAnalyze){
			lysMods.add(new Ion(DC4_DE_Cleaved,"",mTagCleavedName," "," ",PRIMARY_AMINE));
			lysMods.add(new Ion(MtagDeadEnd,"",MtagDeadEndName," "," ",PRIMARY_AMINE));
			outfilename = outfilename + "_mTag";
		}	
		// DC4 mods
		if (DC4Analyze){
			lysMods.add(new Ion(DC4_DE_Cleaved,"",DC4_DE_CleavedName," "," ",PRIMARY_AMINE));
			lysMods.add(new Ion(DC4_DE_1arm,"",DC4_DE_1armName," "," ",PRIMARY_AMINE));
			lysMods.add(new Ion(DC4_DE_Full,"",DC4_DE_FullName," "," ",PRIMARY_AMINE));
			outfilename = outfilename + "_DC4";
			
			// True xlinks
//			xlinks.add(new Ion(DC4xlink,"","DC4-xlink"," "));
			
			// for testing only:
//			xlinks.add(new Ion(DC4_DE_Full_1H,"",DC4_DE_FullName + "_1H"," "," "));
		}	
		// BS3 mods
		if (BS3Analyze){
			lysMods.add(new Ion(BS3DeadEnd,"",BS3_DE_Name," "," ",PRIMARY_AMINE));
			
			// True xlinks
//			xlinks.add(new Ion(DC4xlink,"","DC4-xlink"," "));
			outfilename = outfilename + "_BS3";
		}
		
		if (TMPAnalyze){
			lysMods.add(new Ion(TMP,"",TMP_Name," "," ",PRIMARY_AMINE));
			outfilename = outfilename + "_TMP";
		}
		
		if (DMPAnalyze){
			lysMods.add(new Ion(DMP,"", DMP_Name," "," ",PRIMARY_AMINE));
			lysMods.add(new Ion(DMP_meth, "", DMP_Name_me, " ", " ", PRIMARY_AMINE));
			outfilename = outfilename + "_DMP";
		}
		
		if (TMPPAnalyze){
			lysMods.add(new Ion(TMPP,"",TMPP_Name," "," ",PRIMARY_AMINE));
			outfilename = outfilename + "_TMPP";
		}
		
		if (GEEAnalyze){
			acidicMods.add(new Ion(GEE, "", GEE_Name," "," ", ACIDIC));
			outfilename = outfilename + "_GEE";
		}
		if (GEE_H_Analyze){
			acidicMods.add(new Ion(GEE_hydro, "", GEE_hydro_Name, " ", " ", ACIDIC));
			outfilename = outfilename + "_GEE-H";	
		}
		if (GAAnalyze){
			acidicMods.add(new Ion(GA, "", GA_Name," "," ", ACIDIC));
			outfilename = outfilename + "_GA";
		}
		if (EDCAnalyze){
			acidicMods.add(new Ion(EDC, "", EDC_Name, " ", " ", ACIDIC));
			outfilename = outfilename + "_EDC";
		}
		allMods.addAll(lysMods);
		allMods.addAll(acidicMods);
		adducts.addAll(amineLosses);
		adducts.addAll(waterLosses);
	}
	
	
	/**
	 *  Main method. Generates ion series and saves the results to an output file
	 */
	public static void main(String[] args) {
		PredictorMain predictor = new PredictorMain();
		String myProtein = PredictorMain.protein;
		ArrayList<String> ionTypes = predictor.iontypes;
		
		// charge state array and adduct arraylist
//		int[] charges = {1,2,3,4,5};
//		int[] charges = {1,2,3,4};
		int[] charges = PredictorMain.CHARGES;
//		int[] internalcharges = PredictorMain.INTERNAL_CHARGES;	
		
		// New methods - unified neutral mass generation
		ArrayList<Ion> neutrals = new ArrayList<Ion>();
		ArrayList<Ion> decoys = new ArrayList<Ion>();
		for (String ionType : ionTypes){
			neutrals.addAll(predictor.generateNeutrals(myProtein, false, 0,ionType));
			if (newDecoys){
				decoys.addAll(predictor.generateNeutrals(myProtein, true, PPM_OFFSET, ionType));
			}
		}
		// New code - much better handling of xlinks (only on Lys, allows combinations, etc)
		System.out.println("adding xlinks...");
		ArrayList<Ion> xlinks = predictor.addXlinks(neutrals);
		ArrayList<Ion> decoyxlinks = predictor.addXlinks(decoys);

		// Add adducts
		System.out.println("adding adducts...");
		ArrayList<Ion> xlinksAndAdducts = predictor.addAdducts(xlinks, adducts);
		ArrayList<Ion> decoyxlinksAndAdducts = predictor.addAdducts(decoyxlinks, adducts);

		// Generate charge state series of the b and y neutral masses predicted above
		System.out.println("assigning charges...");
		ArrayList<Ion> FinalIons = predictor.generateCSList(xlinksAndAdducts,charges);
		ArrayList<Ion> decoyFinalIons = predictor.generateCSList(decoyxlinksAndAdducts,charges);

		// Print outputs to file
		System.out.println("printing to file");
		String outfilenameAll = outputDir + File.separator + outfilename + ".csv";
		predictor.printOutputListToFile(FinalIons,outfilenameAll);

		String completeOutFilename = outputDir + File.separator + outfilename + "_Decoy.csv";
		decoyFinalIons.addAll(FinalIons);
		predictor.printOutputListToFile(decoyFinalIons,completeOutFilename);			
//		
//		ArrayList<Ion> recursion = predictor.generateNeutralTree(myProtein, false, 0);
//		ArrayList<Ion> finalrecurs = predictor.generateCSList(recursion, charges);
//		predictor.printOutputListToFile(finalrecurs, outputDir + File.separator + outfilename + "_recurs.csv");
		
		System.out.println("Done!");
	
		// Get ions of b and y ion series for myProtein
//		System.out.println("Generating b,y lists");
//		ArrayList<Ion> bIonNeutrals = predictor.generateBseries(myProtein,false, 0);
//		ArrayList<Ion> yIonNeutrals = predictor.generateYseries(myProtein,false, 0);
//		ArrayList<Ion> allNeutrals = new ArrayList<Ion>();
//		allNeutrals.addAll(bIonNeutrals);
//		allNeutrals.addAll(yIonNeutrals);
//		
//		// New code - much better handling of xlinks (only on Lys, allows combinations, etc)
//		System.out.println("adding xlinks...");
//		ArrayList<Ion> xlinks = predictor.addXlinks(allNeutrals);
//		
//		// Add adducts
//		System.out.println("adding adducts...");
//		ArrayList<Ion> xlinksAndAdducts = predictor.addAdducts(xlinks, adducts);
//		
//		// Generate charge state series of the b and y neutral masses predicted above
//		System.out.println("assigning charges...");
//		ArrayList<Ion> FinalIons = predictor.generateCSList(xlinksAndAdducts,charges);
//		
//		// Print outputs to file
//		System.out.println("printing to file");
//		String outfilenameAll = outputDir + File.separator + outfilename + ".csv";
//		predictor.printOutputListToFile(FinalIons,outfilenameAll);
//		
//		// Create decoy sequence lists as well
//		System.out.println("Generating decoys");
//		ArrayList<Ion> bdecoys; 
//		ArrayList<Ion> adecoys;
//		if (newDecoys){
//			// use ppm offset instead of reversing sequence
//			bdecoys = predictor.generateBseries(myProtein,true,PPM_OFFSET);
//			adecoys = predictor.generateYseries(myProtein,true,PPM_OFFSET);
//		} else {
//			bdecoys = predictor.generateBseries(decoy,true, 0);
//			adecoys = predictor.generateYseries(decoy,true, 0);
//		}	
//		ArrayList<Ion> decoyNeutrals = new ArrayList<Ion>();
//		decoyNeutrals.addAll(bdecoys);
//		decoyNeutrals.addAll(adecoys);
//
//		// New code - much better handling of xlinks (only on Lys, allows combinations, etc)
//		System.out.println("adding xlinks...");
//		ArrayList<Ion> decoyxlinks = predictor.addXlinks(decoyNeutrals);
//
//		// Add adducts
//		System.out.println("adding adducts...");
//		ArrayList<Ion> decoyxlinksAndAdducts = predictor.addAdducts(decoyxlinks, adducts);
//
//		// Generate charge state series of the b and y neutral masses predicted above
//		System.out.println("assigning charges...");
//		ArrayList<Ion> decoyFinalIons = predictor.generateCSList(decoyxlinksAndAdducts,charges);
//
//		// Print outputs to file, and a concatentated list
//		System.out.println("printing to file");
//		String completeOutFilename;
//		if (newDecoys){
//			completeOutFilename = outputDir + File.separator + outfilename + "_newDecoy.csv";
//		} else {
//			completeOutFilename = outputDir + File.separator + outfilename + "_Decoy.csv";
//		}
//		//				predictor.printOutputListToFile(decoyFinalIons,decoyoutfilenameAll);
//		decoyFinalIons.addAll(FinalIons);
//		predictor.printOutputListToFile(decoyFinalIons,completeOutFilename);
//		
//		
//		if (internalIons){
//			// Generate internal ions for b,y-like series	
//			System.out.println("Starting internal ion generation");
//			ArrayList<Ion> IonInternalNeutrals = predictor.generateInternalBs(myProtein,false, 0);
//			ArrayList<Ion> ainternals = predictor.generateInternalAs(myProtein,false, 0);
//			ArrayList<Ion> yterminals = predictor.generateYseries(myProtein, false, 0);
//			IonInternalNeutrals.addAll(ainternals);
//			IonInternalNeutrals.addAll(yterminals);
//			
//			System.out.println("adding xlinks...");
//			ArrayList<Ion> IonInternalXlinks = predictor.addXlinks(IonInternalNeutrals);
//			System.out.println("adding adducts...");
//			ArrayList<Ion> internalxlinksAndAdducts = predictor.addAdducts(IonInternalXlinks,adducts);
//			
//			System.out.println("computing charge states...");
//			ArrayList<Ion> finalInternalIons = predictor.generateCSList(internalxlinksAndAdducts,internalcharges);
//			
//			System.out.println("printing to file");
//			String outfilenameInternal = outputDir + File.separator + outfilename + "_internal.csv";
//			predictor.printOutputListToFile(finalInternalIons,outfilenameInternal);
//			
//			System.out.println("Starting internal ion decoy generation");
//			ArrayList<Ion> IonInternalNeutralsDecoy = predictor.generateInternalBs(myProtein, true, PPM_OFFSET);
//			ArrayList<Ion> ainternalsDecoy = predictor.generateInternalAs(myProtein, true, PPM_OFFSET);
//			ArrayList<Ion> yterminalsDecoy = predictor.generateYseries(myProtein, true, PPM_OFFSET);
//			IonInternalNeutralsDecoy.addAll(ainternalsDecoy);
//			IonInternalNeutralsDecoy.addAll(yterminalsDecoy);
//			
//			System.out.println("adding xlinks...");
//			ArrayList<Ion> IonInternalXlinksDecoy = predictor.addXlinks(IonInternalNeutralsDecoy);
//			System.out.println("adding adducts...");
//			ArrayList<Ion> internalxlinksAndAdductsDecoy = predictor.addAdducts(IonInternalXlinksDecoy,adducts);
//			
//			System.out.println("computing charge states...");
//			ArrayList<Ion> finalInternalIonsDecoy = predictor.generateCSList(internalxlinksAndAdductsDecoy,charges);
//			finalInternalIonsDecoy.addAll(finalInternalIons);
//			
//			System.out.println("printing to file");
//			String outfilenameInternalDecoy = outputDir + File.separator + outfilename + "_internal+decoy.csv";
//			predictor.printOutputListToFile(finalInternalIonsDecoy,outfilenameInternalDecoy);
//			
//		}

		
		
	}
	
	
	/**
	 * New method to add xlinks to ions. Only allows xlinkers to be placed on Lys residues, but
	 * allows as many xlinks as there are Lys's. Also allows combinations of types. 
	 * @param ionNeutrals
	 * @return
	 */
	private ArrayList<Ion> addXlinks(ArrayList<Ion> ionNeutrals){
		ArrayList<Ion> allXlinks = new ArrayList<Ion>();
		ArrayList<Double> masses = new ArrayList<Double>();	 // Holder for mz values for preventing duplicates
		
		//  First, add possible modifications ("xlinks") to the N-terminus
		ArrayList<Ion> newIonNeutrals = new ArrayList<Ion>();
		newIonNeutrals.addAll(ionNeutrals);
				
		// Add N-terminal ions for N-terminal mods for primary amine-modifying chemistries only
		for (Ion xlinkerInfo : lysMods){
			if (xlinkerInfo.getTargets().contains("K")){
				for (Ion oldIon : ionNeutrals){
					if (oldIon.getType().startsWith("b") || oldIon.getType().startsWith("a") || oldIon.getType().startsWith("c") || oldIon.getType().startsWith("d")){
						Ion newIon;
						// only b ions include the N-terminus and can be modified
						double newNeutralMass = oldIon.getNeutralMass() + xlinkerInfo.getNeutralMass();
						newIon = new Ion(newNeutralMass,oldIon.getSequence(),oldIon.getType(),"",xlinkerInfo.getXlinks(), oldIon.getDecoy());				
						newIonNeutrals.add(newIon);
					}
				}
			}
		}
		
		// Loop through the array of neutral ions to add xlinks on Lys residues
		for (Ion initialIon : newIonNeutrals){
			// Add unmodified ion to list
			allXlinks.add(initialIon);
					
			String seq = initialIon.getSequence();
			masses.add(initialIon.getNeutralMass());
			ArrayList<Ion> allPossibleXlinks = new ArrayList<Ion>();
			// Ignore extremely long fragments to reduce length of processing
			if (seq.length() < MAX_FRAGMENT_LENGTH){
				allPossibleXlinks.add(initialIon);
				for (int i = 0; i < seq.length(); i++){
					
					String currentAA = seq.substring(i, i+1);
					// Create ions for each possible xlink result and combination using helper method
					for (Ion xLinker : allMods){
						if (xLinker.getTargets().contains(currentAA.toUpperCase())){
							// This AA is a target of this modifying chemistry, so add its modified version to the possibilities list
							ArrayList<Ion> newPossibilities = createXlinkNeutrals(allPossibleXlinks, xLinker);
							allPossibleXlinks.addAll(newPossibilities);
						}
					}
					
				}
				// Add all possible xlinks for this Ion to the list of all xlinks for all Ions
				for (Ion newIon : allPossibleXlinks){
					if (! masses.contains(newIon.getNeutralMass())){
						allXlinks.add(newIon);
						masses.add(newIon.getNeutralMass());
					}
				}
			} else {
				// For very long ions, only keep the unmodified version (do nothing here)
			}
		}
		return allXlinks;
	}
	
	private ArrayList<Ion> createXlinkNeutrals(ArrayList<Ion> originalList, Ion xlinkerInfo){
		ArrayList<Ion> newList = new ArrayList<Ion>();
		// Unmodified ions already included in the final list, no need to add again
//		newList.addAll(originalList);
		
		// for each ion in the possible xlinks list, add a new ion for each possible type of mod
		for (Ion oldIon : originalList){
//			for (Ion xlinkerInfo : modlist){
				// Compute new neutral mass (list will still contain the unmodified ion, so no need to add it)
				double newNeutralMass = oldIon.getNeutralMass() + xlinkerInfo.getNeutralMass();
				Ion newIon;
				// Add new modification to existing ones rather than replacing if there are existing mods. 
				if (oldIon.getXlinks().length() > 0){
					newIon = new Ion(newNeutralMass,oldIon.getSequence(),oldIon.getType(),"",oldIon.getXlinks()+ "," + xlinkerInfo.getXlinks(), oldIon.getDecoy());
				} else {
					newIon = new Ion(newNeutralMass,oldIon.getSequence(),oldIon.getType(),"",xlinkerInfo.getXlinks(), oldIon.getDecoy());
				}
				 
				newIon.setAdducts(oldIon.getAdducts());
				if (!newList.contains(newIon)){
					newList.add(newIon);
				}
//			}
		}
		return newList;
	}
	
	
	/**
	 * Adds adduct masses to neutral ions. To be done BEFORE assigning charges
	 * @param ionNeutrals
	 * @param adducts = the adduct masses and identities (use sequence field for ID) of
	 * possible adducts
	 * @return
	 */
	private ArrayList<Ion> addAdducts(ArrayList<Ion> ionNeutrals, ArrayList<Ion> adducts){
		ArrayList<Ion> adductsList = new ArrayList<Ion>();
		
		// for each peptide ion, create an additional ion for each xlink in the xlink list
		for (Ion currentOldion : ionNeutrals){
			// Also include un-xlinked peptides from original list
			adductsList.add(currentOldion);
	
			// Add an new ion for each possible adduct to this neutral mass
			for (Ion adduct : adducts){
				double newNeutralMass = currentOldion.getNeutralMass() + adduct.getNeutralMass();
				// check for appropriate losses
				if (adduct.getNeutralMass() == H2O){
					// should only occur in peptides containing -OH (STED)
					if (peptideContains(currentOldion.getSequence(), "S,T,E,D", 1)){
						// create a new ion if so, ignore if not
						Ion newIon = new Ion(newNeutralMass,currentOldion.getSequence(),currentOldion.getType(),adduct.getAdducts(),currentOldion.getXlinks(),currentOldion.getDecoy());
						adductsList.add(newIon);
					}
					
				} else if (adduct.getNeutralMass() == NH3){
					if (peptideContains(currentOldion.getSequence(), "R,N,K,Q", 1)){
						Ion newIon = new Ion(newNeutralMass,currentOldion.getSequence(),currentOldion.getType(),adduct.getAdducts(),currentOldion.getXlinks(),currentOldion.getDecoy());
						adductsList.add(newIon);
					}
					
				} else if (adduct.getNeutralMass() == NH3andH2O){
					if (peptideContains(currentOldion.getSequence(), "R,N,K,Q", 1) && peptideContains(currentOldion.getSequence(), "S,T,E,D", 1)){
						Ion newIon = new Ion(newNeutralMass,currentOldion.getSequence(),currentOldion.getType(),adduct.getAdducts(),currentOldion.getXlinks(),currentOldion.getDecoy());
						adductsList.add(newIon);
					}
					
				} else if (adduct.getNeutralMass() == NH3x2){
					if (peptideContains(currentOldion.getSequence(), "R,N,K,Q", 2)){
						Ion newIon = new Ion(newNeutralMass,currentOldion.getSequence(),currentOldion.getType(),adduct.getAdducts(),currentOldion.getXlinks(),currentOldion.getDecoy());
						adductsList.add(newIon);
					}
					
				} else if (adduct.getNeutralMass() == H2Ox2){
					if (peptideContains(currentOldion.getSequence(), "S,T,E,D", 2)){
						Ion newIon = new Ion(newNeutralMass,currentOldion.getSequence(),currentOldion.getType(),adduct.getAdducts(),currentOldion.getXlinks(),currentOldion.getDecoy());
						adductsList.add(newIon);
					}
					
				} else {
					// for all other adducts, simply create the new ion
					Ion newIon = new Ion(newNeutralMass,currentOldion.getSequence(),currentOldion.getType(),adduct.getAdducts(),currentOldion.getXlinks(),currentOldion.getDecoy());
					adductsList.add(newIon);
				}
			}
		}
		return adductsList;
	}


	/**
		 * 
		 * @param ionNeutrals
		 * @param charges
		 * @return
		 */
		private ArrayList<Ion> generateCSList(ArrayList<Ion> ionNeutrals, int[] charges){
			
			ArrayList<Ion> finalIons = new ArrayList<Ion>();
			
			// For each charge, compute the ion masses from the neutral list and initialize a new ion with all information
	//		int counter = 0;
			for (int charge : charges){		
				for (Ion neutralIon : ionNeutrals){
	//				System.out.println("Ion Neutral " + counter + " of " + ionNeutrals.size()*charges.length);
	//				counter++;
					// Compute the ion (charged) mass based on charge and neutral mass
					double ionmass = (neutralIon.getNeutralMass() + ((double)charge * ProtonMass))/(double)charge;
	//				BigDecimal ionmass = 
					if (ionmass < MZ_CUTOFF && ionmass > MZ_MIN){
						// Pass all information to the final Ion array (catch uninitialized xlinks/adducts
						Ion finalIon = null;
						try{
							finalIon = new Ion(neutralIon.getNeutralMass(),neutralIon.getSequence(),ionmass,charge,neutralIon.getType(),neutralIon.getXlinks(),neutralIon.getAdducts(), neutralIon.getDecoy());
						} catch (NullPointerException ex){
							finalIon = new Ion(neutralIon.getNeutralMass(),neutralIon.getSequence(),ionmass,charge,neutralIon.getType(),"","", neutralIon.getDecoy());
							System.out.println("no xlinks or adducts");
						}
	
						finalIons.add(finalIon);
					}
					
				}		
			}
			return finalIons;
		}


	/**
	 * prints the specified Ion array to the filename. Filename MUST be a complete path
	 * @param printarray
	 * @param filedir
	 */
	public void printOutputListToFile(ArrayList<Ion> printarray, String filedir){
		 File outfile = new File(filedir);
	     String linesep = System.getProperty("line.separator");
	     
         try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
			// Write the header
			String header = "Ion Mass (m/z),Charge,Sequence,Ion Type,Neutral Mass (Da),Adduct(s),Decoy?,Xlink(s)";
			writer.write(header);
			writer.write(linesep);
			
			for (Ion currentIon  : printarray){
				// Get the information to print from the ion
				int charge = currentIon.getCharge();
				double neutralmass = currentIon.getNeutralMass();
				double ionMass = currentIon.getIonMass();
				String sequence = currentIon.getSequence();
				String ionType = currentIon.getType();
				String xlinks = currentIon.getXlinks();
				String adducts = currentIon.getAdducts();
				String decoy = currentIon.getDecoy();
//				try{
//					adducts = currentIon.getAdducts();
//				} catch (NullPointerException ex){
//					adducts = "";
//				}
	
				// Write the information to file as a csv (comma delimited)
				String line = String.valueOf(ionMass) + "," + String.valueOf(charge) + "," + sequence + "," + 
						ionType + "," + String.valueOf(neutralmass) + "," + adducts + "," + decoy + "," + xlinks;
				writer.write(line);
				writer.write(linesep);
				
			}
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * New method (INCOMPLETE - 5/9/16) to provide much faster computation of theoretical sequences
	 * by building mods/losses/etc into subsequences as they're built rather than after the fact.
	 * Probably needs recursive helper method to deal with branches.
	 * @param myProtein
	 * @param decoy
	 * @param ppmOFfset
	 * @return
	 */
	private ArrayList<Ion> generateNeutralTree (String myProtein, boolean decoy, double ppmOFfset){
		ArrayList<Ion> sequences = new ArrayList<Ion>();
		ArrayList<Ion> finalNeutrals = new ArrayList<Ion>();
		
		// n-terminal ions
		String firstAA = myProtein.substring(0,1);
		Ion firstIon = new Ion(determinePeptideNeutralMass(firstAA), firstAA,"", " ", " ", "");
		sequences.add(firstIon);
		//newIon = new Ion(newNeutralMass,oldIon.getSequence(),oldIon.getType(),"",oldIon.getXlinks()+ "," + xlinkerInfo.getXlinks(), oldIon.getDecoy());

		// Generate Ions for all sequences, including losses and modifications
		sequences.addAll(recursiveBranch(myProtein, firstIon, 1,sequences));
		
		// c-terminal ions
		
		
		// Finalize ions by adding type information for all desired ion types
		finalNeutrals = addIonTypes(sequences);
		
		
		return finalNeutrals;
	}
	
	private ArrayList<Ion> addIonTypes(ArrayList<Ion> sequences){
		ArrayList<Ion> finalNeutrals = new ArrayList<Ion>();
		// Add all desired ion types to each Ion
		for (Ion ion : sequences){
			for (String type : iontypes){
				finalNeutrals.addAll(getNeutralMass(ion, false, 0.0, type));
			}
		}
		
		return finalNeutrals;
	}
	
	/**
	 * Recursive helper method for neutral tree method. Depth-first "search" for all ions and mod
	 * types. Gets all possible children/branches for a given ion (node) from second helper method,
	 * then recurses through each of them. Designed to run until depth reaches protein.length (or 0 if
	 * running from c-terminal to n)
	 * @param currentIon
	 * @param depth
	 */
	private ArrayList<Ion> recursiveBranch(String fullSequence, Ion currentIon, int depth, ArrayList<Ion> branches){
//		ArrayList<Ion> newBranches = new ArrayList<Ion>();
		
		while (depth < fullSequence.length()){
			// Get the next AA in the sequence
			String nextAA = fullSequence.substring(depth, depth + 1);
			
			// Get all branches from this ion (branches depend on next AA and user settings)
			ArrayList<Ion> childIons = getBranches(currentIon, nextAA);
			branches.addAll(childIons);
			for (int i=0; i < childIons.size(); i++){
//			for (Ion child : childIons){
				recursiveBranch(fullSequence, childIons.get(i), depth + 1, branches);
				return branches;
			}
		}
		
		return branches;
	}
	
	
	private ArrayList<Ion> getBranches(Ion prevIon, String nextAA){
		ArrayList<Ion> nextBranches = new ArrayList<Ion>();
		String newSequence = prevIon.getSequence() + nextAA;
		
		// Always add the basic next branch (next AA with no additional mods/adducts)
		Ion nextIon = new Ion(determinePeptideNeutralMass(newSequence),newSequence,"",prevIon.getAdducts(),prevIon.getXlinks(), prevIon.getDecoy());
		nextBranches.add(nextIon);
		
		// Determine which (if any) other branches to add based on the user settings and AA possibilities
		switch(nextAA){
		case "K":
			// Can have all Lys-targeting chemistries and NH3 loss. CAN'T have Lys mod and NH3 loss at same time, so they're done separately
			for (Ion mod : lysMods){
				double newNeutralMass = prevIon.getNeutralMass() + mod.getNeutralMass();
				Ion branch = new Ion(newNeutralMass,newSequence,"",prevIon.getAdducts(),prevIon.getXlinks()+ "," + mod.getXlinks(), prevIon.getDecoy());
				nextBranches.add(branch);
			}
			for (Ion loss: amineLosses){
				double newNeutralMass = prevIon.getNeutralMass() + loss.getNeutralMass();
				Ion branch = new Ion(newNeutralMass,newSequence,"",prevIon.getAdducts() + loss.getAdducts(),prevIon.getXlinks(), prevIon.getDecoy());
				nextBranches.add(branch);
			}
			break;
		case "R":
			for (Ion loss: amineLosses){
				double newNeutralMass = prevIon.getNeutralMass() + loss.getNeutralMass();
				Ion branch = new Ion(newNeutralMass,newSequence,"",prevIon.getAdducts() + loss.getAdducts(),prevIon.getXlinks(), prevIon.getDecoy());
				nextBranches.add(branch);
			}
			break;
		case "Q":
			for (Ion loss: amineLosses){
				double newNeutralMass = prevIon.getNeutralMass() + loss.getNeutralMass();
				Ion branch = new Ion(newNeutralMass,newSequence,"",prevIon.getAdducts() + loss.getAdducts(),prevIon.getXlinks(), prevIon.getDecoy());
				nextBranches.add(branch);
			}
			break;
		case "N":
			for (Ion loss: amineLosses){
				double newNeutralMass = prevIon.getNeutralMass() + loss.getNeutralMass();
				Ion branch = new Ion(newNeutralMass,newSequence,"",prevIon.getAdducts() + loss.getAdducts(),prevIon.getXlinks(), prevIon.getDecoy());
				nextBranches.add(branch);
			}
			break;
		case "S":
			for (Ion loss: waterLosses){
				double newNeutralMass = prevIon.getNeutralMass() + loss.getNeutralMass();
				Ion branch = new Ion(newNeutralMass,newSequence,"",prevIon.getAdducts() + loss.getAdducts(),prevIon.getXlinks(), prevIon.getDecoy());
				nextBranches.add(branch);
			}
			break;
		case "T":
			for (Ion loss: waterLosses){
				double newNeutralMass = prevIon.getNeutralMass() + loss.getNeutralMass();
				Ion branch = new Ion(newNeutralMass,newSequence,"",prevIon.getAdducts() + loss.getAdducts(),prevIon.getXlinks(), prevIon.getDecoy());
				nextBranches.add(branch);
			}
			break;
		case "E":
			for (Ion mod : acidicMods){
				double newNeutralMass = prevIon.getNeutralMass() + mod.getNeutralMass();
				Ion branch = new Ion(newNeutralMass,newSequence,"",prevIon.getAdducts(),prevIon.getXlinks()+ "," + mod.getXlinks(), prevIon.getDecoy());
				nextBranches.add(branch);
			}
			for (Ion loss: waterLosses){
				double newNeutralMass = prevIon.getNeutralMass() + loss.getNeutralMass();
				Ion branch = new Ion(newNeutralMass,newSequence,"",prevIon.getAdducts() + loss.getAdducts(),prevIon.getXlinks(), prevIon.getDecoy());
				nextBranches.add(branch);
			}
			break;
		case "D":
			for (Ion mod : acidicMods){
				double newNeutralMass = prevIon.getNeutralMass() + mod.getNeutralMass();
				Ion branch = new Ion(newNeutralMass,newSequence,"",prevIon.getAdducts(),prevIon.getXlinks()+ "," + mod.getXlinks(), prevIon.getDecoy());
				nextBranches.add(branch);
			}
			for (Ion loss: waterLosses){
				double newNeutralMass = prevIon.getNeutralMass() + loss.getNeutralMass();
				Ion branch = new Ion(newNeutralMass,newSequence,"",prevIon.getAdducts() + loss.getAdducts(),prevIon.getXlinks(), prevIon.getDecoy());
				nextBranches.add(branch);
			}
			break;
		}

		return nextBranches;
	}
	
	/**
	 * New method to generate all ion series in one place. Has different substring order generators 
	 * for series originating at N and C termini (and internals (?)) and calls a helper method that 
	 * generates masses given a substring sequence and ion type
	 */
	private ArrayList<Ion> generateNeutrals(String myProtein, boolean decoy, double ppmOffset, String ionType){
		// Generate the sub-sequences corresponding to each possible C-terminal or N-terminal ion
//		String[] subStrings = getSubSequences(myProtein, ionType);
		ArrayList<String> subStrings = getSubSequences(myProtein, ionType);
		
		// Get the neutral masses of all the sub-sequences, including the C-terminal OH expected for a y-ion
		return getNeutralMasses(subStrings, decoy, ppmOffset, ionType);
	}
	
	/**
	 * Helper method for generating substrings of a protein sequence from N or C termini or internal. Returns
	 * a String Arraylist of each terminal sequence for the corresponding terminal type (N-term for a,b,d; 
	 * C-term for x,y,v) or of all internal combinations for internal types
	 */
	private ArrayList<String> getSubSequences(String myProtein, String ionType){
		ArrayList<String> subStrings = new ArrayList<String>();

		if (ionType.matches("y") || ionType.matches("x") || ionType.matches("v") || ionType.matches("z") || ionType.matches("w")){
			for (int i = myProtein.length()-1; i >= 0; i--){
				String subString = myProtein.substring(i, myProtein.length());
				subStrings.add(subString);
			}
		} else if (ionType.matches("b") || ionType.matches("a") || ionType.matches("d") || ionType.matches("c")){
			for (int i = 1; i < protein.length(); i++){
				String subString = protein.substring(0, i);
				subStrings.add(subString);
			}
			subStrings.add(protein);
		} else if (ionType.matches("int-a") || ionType.matches("int-b")){
			// internal ion substrings
			for (int i = protein.length(); i >= 0; i--){
				int j=0;
				while ((i-j) >= 0){
					String subString = protein.substring(i-j, i);
					subStrings.add(subString);
					j++;
				}				
			}	
		}		
		else {
			System.out.println("Invalid ion type was: " + ionType);
			System.out.println("No prediction performed for that ion type");
		}
		return subStrings;
	}


	/**
	 * Helper method to compute neutral mass of a list of ions/fragments given an ion type
	 */
	private ArrayList<Ion> getNeutralMasses(ArrayList<String> subStrings, boolean decoy, double ppmOffset, String ionType){
		ArrayList<Ion> neutrals = new ArrayList<Ion>();
		// Loop through the substrings and compute neutral mass based on ion type
		for (String subString : subStrings){
			neutrals.addAll(getNeutralMass(new Ion(subString), decoy, ppmOffset, ionType));
		}
		return neutrals;
	}
	
	private ArrayList<Ion> getNeutralMass(Ion sequence, boolean decoy, double ppmOffset, String ionType){
		String subString = sequence.getSequence();
		ArrayList<Ion> neutrals = new ArrayList<Ion>();

		int length = subString.length();
		String ionName = "";
		double massChanges = -1;
		double massChanges2 = -1;
		
		switch(ionType){
		case "y":
			ionName = "y";
			massChanges = Hydrogen + Oxygen + Hydrogen;
			break;
		case "b":
			ionName = "b";
			massChanges = 0;
			break;
		case "x":
			ionName = "x";
			massChanges = Carbon + Oxygen + Oxygen;
			break;
		case "a":
			ionName = "a";
			massChanges = CO_loss;
			break;
		case "c":
			ionName = "c";
			massChanges = Nitrogen + Hydrogen + Hydrogen + Hydrogen;
			break;
		case "z":
			ionName = "z";
			massChanges = Oxygen - Nitrogen;
			break;
		case "d":
			ionName = "d";
			// get the side chain at the c-terminal side of the peptide, as it is an N-terminal fragment
			String pepCTerm = subString.substring(length-1);
			// Check to make sure this loss can happen - avoid G,A,P,F,Y,W residues
			if (! peptideContains(pepCTerm, "G,A,P,F,Y,W", 1)){
				massChanges = getSideChainLoss(pepCTerm,"d", 0);
				if (peptideContains(pepCTerm,"I,T",1)){
					massChanges2 = getSideChainLoss(pepCTerm,"d", 1);
				}
			}
			break;
		case "v":
			ionName = "v";
			// Get the side chain at the "n-terminus" of this c-terminal peptide
			String nTerm = subString.substring(0, 1);
			massChanges = getSideChainLoss(nTerm, "v", 0);
			break;
		case "w":
			ionName = "w";
			String cTerm = subString.substring(0, 1);
			// Check to make sure this loss can happen - avoid G,A,P,F,Y,W residues where it does not occur (P = ??)
			if (! peptideContains(cTerm, "G,A,P,F,Y,W", 1)){
				massChanges = getSideChainLoss(cTerm,"w", 0);
				if (peptideContains(cTerm,"I,T",1)){
					massChanges2 = getSideChainLoss(cTerm,"w", 1);
				}
			}
			break;
		case "int-a":
			ionName = "ai";
			massChanges = CO_loss;
			break;
		case "int-b":
			ionName = "bi";
			massChanges = 0;
			break;
		}	
		
		// compute type-specific mass changes
		double neutralMass = determinePeptideNeutralMass(subString) + massChanges;	
		
		// Initialize a new ion with all appropriate information. If massChanges is -1, ignore this ion as whatever loss was attempted doesn't happen
		Ion myIon;
		if (!(massChanges == -1)){
			if(decoy){
				double massOffset = neutralMass * (ppmOffset/1000000);
				myIon = new Ion(neutralMass + massOffset,subString,ionName + length,sequence.getAdducts(),sequence.getXlinks(),"DECOY");
			} else {
				myIon = new Ion(neutralMass,subString,ionName + length,sequence.getAdducts(),sequence.getXlinks(),"");
			}			
//			myIon.setAdducts("");
//			myIon.setXlinks("");
			neutrals.add(myIon);
		}
		Ion my2Ion;
		if (!(massChanges2 == -1)){
			// We have a second type of fragment, include it
			double neutralMass2 = neutralMass - massChanges + massChanges2;
			if(decoy){
				double massOffset = neutralMass * (ppmOffset/1000000);
				my2Ion = new Ion(neutralMass2 + massOffset,subString,ionName + length,sequence.getAdducts(),sequence.getXlinks(),"DECOY");
			} else {
				my2Ion = new Ion(neutralMass2,subString,ionName + length,sequence.getAdducts(),sequence.getXlinks(),"");
			}			
//			my2Ion.setAdducts("");
//			my2Ion.setXlinks("");
			neutrals.add(my2Ion);
		}
		
		return neutrals;
	}
	
	/**
	 * Helper method for side chain specific losses from v and d type ions (may add w eventually if
	 * needed). For v ions, computes side chain loss by subtracting peptide backbone mass from total
	 * AA residue mass.
	 */
	private double getSideChainLoss(String terminalRes, String ionType, int typeNum){
		double massChange = 0;
		if (ionType.matches("v")){
			// subtract peptide backbone mass from total AA mass to get the side chain mass
			// v-ions also have c-terminal OH like y-ions. Mass change is side chain mass MINUS the terminal OH since we're subtracting it from the peptide mass
			massChange = getmass(terminalRes.charAt(0)) - PEPTIDE_BACKBONE_MASS - Oxygen - Hydrogen;
			
		} else if (ionType.matches("d")){
			// subtract peptide backbone mass except one methyl group and CO, except for I, T, and V
			if (terminalRes.toUpperCase().matches("I")){
				// Ile retains CH2 from side chain OR CH2CH2 depending on which bond breaks
				massChange = getmass(terminalRes.charAt(0)) - PEPTIDE_BACKBONE_MASS - (Carbon + Hydrogen + Hydrogen + Hydrogen) - (Carbon + Hydrogen + Hydrogen) - CO_loss; // matches Protein Prospector's "Da" ion
				if (typeNum == 1){
					massChange = getmass(terminalRes.charAt(0)) - PEPTIDE_BACKBONE_MASS - (Carbon + Hydrogen + Hydrogen + Hydrogen) - (Carbon + Hydrogen + Hydrogen) - (Carbon + Hydrogen + Hydrogen) - CO_loss; // matches Protein Prospector's "Db" ion
				}
			} else if (terminalRes.toUpperCase().matches("T")){
				// Thr retains O from side chain OR CH2 depending on which bond breaks
				massChange = getmass(terminalRes.charAt(0)) - PEPTIDE_BACKBONE_MASS - (Carbon + Hydrogen + Hydrogen + Hydrogen) - (Carbon + Hydrogen + Hydrogen) - CO_loss; // matches Protein Prospector's "Da" ion
				if (typeNum == 1){
					massChange = getmass(terminalRes.charAt(0)) - PEPTIDE_BACKBONE_MASS - (Carbon + Hydrogen + Hydrogen + Hydrogen) - (Oxygen) - CO_loss; // matches Protein Prospector's "Da" ion
				}
			} else if (terminalRes.toUpperCase().matches("V")){
				// Val retains CH2 from side chain
				massChange = getmass(terminalRes.charAt(0)) - PEPTIDE_BACKBONE_MASS - (Carbon + Hydrogen + Hydrogen + Hydrogen) - (Carbon + Hydrogen + Hydrogen) - CO_loss; // matches Protein Prospector's "Da" ion
			} else {
				// typical d-ion = a-ion mass (sequence mass - CO) + one methyl group (CH3)
				massChange = getmass(terminalRes.charAt(0)) - PEPTIDE_BACKBONE_MASS - (Carbon + Hydrogen + Hydrogen + Hydrogen) - CO_loss; // COloss is negative
			}
		} else if (ionType.matches("w")){
			massChange = (getmass(terminalRes.charAt(0)) - PEPTIDE_BACKBONE_MASS) - (Carbon + Hydrogen + Hydrogen + Hydrogen) + Nitrogen + Hydrogen + Hydrogen - (Oxygen + Hydrogen);
			if (terminalRes.toUpperCase().matches("I")){
				massChange = massChange - (Carbon + Hydrogen + Hydrogen);
				if (typeNum == 1) massChange = massChange - (Carbon + Hydrogen + Hydrogen);
			} else if (terminalRes.toUpperCase().matches("T")){	
				if (typeNum == 1){
					massChange = massChange - (Carbon + Hydrogen + Hydrogen);
				} else {
					massChange = massChange - Oxygen;
				}
			} else if (terminalRes.toUpperCase().matches("V")){
				massChange = massChange - (Carbon + Hydrogen + Hydrogen);
			}

		}
		// All of these changes are negative (losses), so return the final calculated loss as a negative
		return (0 - massChange);
	}

	
	/**
	 *  Determine neutral mass of a given string sequence NOT including terminal H and OH
	 * @param sequence: the AA sequence
	 * @return a double of the sequence neutral mass (not including terminal H,OH)
	 */
		public double determinePeptideNeutralMass(String sequence){
			// Does NOT include peptide terminal H, OH since we're using them to compute fragment masses
			double finalmass = 0;	
			int sequenceLength = sequence.length();
			
			// Get the amino acids into a char array
			char[] aminoacids = new char[sequenceLength];
			sequence.getChars(0, sequence.length(), aminoacids, 0);
			
			// Loop through the sequence, adding masses
			for (char aa : aminoacids){
				finalmass = finalmass + getmass(aa);
			}		
			return finalmass;
		}
	
	/**
	 * Helper method to determine if a given sequence contains at least the specified number
	 * of the given residues (e.g. S,T,E,D check with 2 needed (e.g. 2 NH3 losses) would return true
	 * for SSAAA but false for SAAAA)
	 * @param sequence
	 * @param checkReses
	 * @return
	 */
	private boolean peptideContains(String mySequence, String checkReses, int numNeeded){
		int numFound = 0;
		String sequence = mySequence.toUpperCase();
		String[] residues = checkReses.split(",");
		for (String res : residues){
			// Loop through the peptide sequence, counting hits to the currect residue we're checking
			for (int i=0; i < sequence.length(); i++){
				if (sequence.charAt(i) == res.charAt(0)){
					numFound++;
					if (numFound >= numNeeded){
						return true;
					}
				}
			}
		}		
		// return false if not enough found to return true
		return false;
	}


	/**
	 *  Return the mass of an amino acid
	 * @param aminoacid
	 * @return
	 */
	public double getmass(char aminoacid){
		double mass = 0;
		
		switch(aminoacid){
			case 'A':
				mass = PredictorMain.A;
				break;
			case 'R':
				mass = PredictorMain.R;
				break;
			case 'N':
				mass = PredictorMain.N;
				break;
			case 'X':
				mass = PredictorMain.X;		// fully glycosylated N-17 in Avidin
				break;
			case 'D':
				mass = PredictorMain.D;
				break;
			case 'C':
				mass = PredictorMain.C;
				break;
			case 'E':
				mass = PredictorMain.E;
				break;
			case 'Q':
				mass = PredictorMain.Q;
				break;
			case 'G':
				mass = PredictorMain.G;
				break;
			case 'H':
				mass = PredictorMain.H;
				break;
			case 'I':
				mass = PredictorMain.I;
				break;
			case 'L':
				mass = PredictorMain.L;
				break;
			case 'K': 
				mass = PredictorMain.K;
				break;
			case 'M':
				mass = PredictorMain.M;
				break;
			case 'F':
				mass = PredictorMain.F;
				break;
			case 'P':
				mass = PredictorMain.P;
				break;
			case 'S':
				mass = PredictorMain.S;
				break;
			case 'T':
				mass = PredictorMain.T;
				break;
			case 'W':
				mass = PredictorMain.W;
				break;
			case 'Y':
				mass = PredictorMain.Y;
				break;
			case 'V':
				mass = PredictorMain.V;
				break;
			case 'Z':
				mass = PredictorMain.Z;
				break;
			case 'J':
				mass = PredictorMain.J;
				break;
			
		}
		
		// Catch bad characters
		if (mass == 0){
			System.out.println("Bad character entered, mass not added for char " + aminoacid);
			System.exit(0);
		}
		
		return mass;
				
	}
	
}

///**
//*  Generates NEUTRAL MASSES of all possible y-ions for this sequence, including H, OH on termini
//* @param myProtein: AA sequence of the protein of interest
//* @return yIons: double array of y ion NEUTRAL masses
//*/
//private ArrayList<Ion> generateYseries(String myProtein, boolean decoy, double ppmOffset) {
//	ArrayList<Ion> yIons = new ArrayList<Ion>();
//	
//	// Generate the sub-sequences corresponding to each possible y-ion
//	String[] subStrings = new String[myProtein.length()];
//	for (int i = myProtein.length()-1; i >= 0; i--){
//		String subString = myProtein.substring(i, myProtein.length());
//		subStrings[i] = subString;
//	}
//
//	// Get the neutral masses of all the sub-sequences, including the C-terminal OH expected for a y-ion
//	for (String subString : subStrings){
//		int length = subString.length();
//		double yIonMass = determinePeptideNeutralMass(subString) + Hydrogen + Oxygen + Hydrogen;
//		Ion yIon;		
//		double massOffset = yIonMass * (ppmOffset/1000000);
//		if(decoy){
//			yIon = new Ion(yIonMass + massOffset,subString,"y" + length," "," ","DECOY");
//		} else {
//			yIon = new Ion(yIonMass,subString,"y" + length," "," ","");
//		}
//		yIon.setAdducts("");
//		yIon.setXlinks("");
//		yIons.add(yIon);
//	}
//	return yIons;
//}
//
///**
//*  Generates NEUTRAL MASSES of all possible b-ions for this sequence
//* @param protein: AA sequence of interest
//* @return double[] containing b ion NEUTRAL masses
//*/
//public ArrayList<Ion> generateBseries(String protein, boolean decoy, double ppmOffset){
//	ArrayList<Ion> bIons = new ArrayList<Ion>();
//	
//	// Generate the sub-sequences corresponding to each possible b-ion
//	String[] subStrings = new String[protein.length()];
//	for (int i = 1; i < protein.length(); i++){
//		String subString = protein.substring(0, i);
//		subStrings[i-1] = subString;
//	}
//	subStrings[protein.length() - 1] = protein;
//	
//	// Get the neutral masses of all the substrings 
//	for (String subString : subStrings){
//		int length = subString.length();
//		if (length >= MIN_FRAGMENT_LENGTH){
//			double bIonMass = determinePeptideNeutralMass(subString);
//			Ion bIon;
//			double massOffset = bIonMass * (ppmOffset/1000000);
//			if(decoy){
//				bIon = new Ion(bIonMass + massOffset,subString,"b" + length,"","","DECOY");
//			} else {
//				bIon = new Ion(bIonMass,subString,"b" + length,"","","");
//			}
//			bIon.setAdducts("");
//			bIon.setXlinks("");
//			bIons.add(bIon);
//		}
//	}
//	return bIons;
//}
//
//private ArrayList<Ion> generateInternalAs(String protein, boolean decoy, double ppmOffset){
//	ArrayList<Ion> neutralList = new ArrayList<Ion>();
//	ArrayList<String> subStrings = new ArrayList<String>();
//	
//
//	// Compute all possible internal substring combinations of this sequence, starting from C-terminus
//	for (int i = protein.length(); i >= 0; i--){
//		int j=0;
//		while ((i-j) >= 0){
//			String subString = protein.substring(i-j, i);
//			subStrings.add(subString);
//			j++;
//		}
//		
//	}
//	// Get the neutral masses of all the sub-sequences and subtract CO to make an 'a' ion
//	for (String subString : subStrings){
//		int length = subString.length();
//		if (length >= MIN_FRAGMENT_LENGTH){
//			double yIonMass = determinePeptideNeutralMass(subString) + CO_loss;
//			Ion yIon;
//			double massOffset = yIonMass * (ppmOffset/1000000);
//			if (subString.endsWith(protein.substring(protein.length() - 1, protein.length()))){
//				// This is a true y ion since it includes the full c-terminus, so name it 'y'
//				if(decoy){
//					yIon = new Ion(yIonMass + massOffset,subString,"ay" + length,"","","DECOY");
//				} else {
//					yIon = new Ion(yIonMass,subString,"ay" + length,"","","");
//				}
//			} else {
//				// this is an internal ion, name it 'i' for internal y ion
//				if(decoy){
//					yIon = new Ion(yIonMass + massOffset,subString,"ai" + length,"","","DECOY");
//				} else {
//					yIon = new Ion(yIonMass,subString,"ai" + length,"","","");
//				}
//			}				
//			yIon.setAdducts("");
//			yIon.setXlinks("");
//			neutralList.add(yIon);
//		}
//	}
//	return neutralList;
//}
//
//private ArrayList<Ion> generateInternalBs(String protein, boolean decoy, double ppmOffset){
//	ArrayList<Ion> neutralList = new ArrayList<Ion>();
//	ArrayList<String> subStrings = new ArrayList<String>();
//			
//	for (int i=0; i < protein.length(); i++){
//		// Compute all possible internal substring combinations of this sequence, starting from N-terminus
//		int j=1;
//		while ((i+j) <= protein.length()){
//			String subString = protein.substring(i,i+j);
//			subStrings.add(subString);
//			j++;
//		}
//		
//	}
////	int counter = 0;
//	// Get the neutral masses of all the substrings 
//	for (String subString : subStrings){
//		int length = subString.length();
//		if (length >= MIN_FRAGMENT_LENGTH){
//			double bIonMass = determinePeptideNeutralMass(subString);
//			Ion bIon;
//			double massOffset = bIonMass * (ppmOffset/1000000);
//			if (subString.startsWith(protein.substring(0, 1))){
//				// This is a true b ion since it includes the full n-terminus, so name it 'b'
//				if(decoy){
//					bIon = new Ion(bIonMass + massOffset,subString,"b" + length,"","","DECOY");
//				} else {
//					bIon = new Ion(bIonMass,subString,"b" + length,"","","");
//				}
//			} else {
//				// this is an internal ion, name it 'j' for internal b ion
//				if(decoy){
//					bIon = new Ion(bIonMass + massOffset,subString,"bi" + length,"","","DECOY");
//				} else {
//					bIon = new Ion(bIonMass,subString,"bi" + length,"","","");
//				}
//			}
//			bIon.setAdducts("");
//			bIon.setXlinks("");			
//			neutralList.add(bIon);	
//		}
//	}
//	return neutralList;
//}
//
