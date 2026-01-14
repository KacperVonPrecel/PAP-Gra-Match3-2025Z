package pap.project;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pap.project.auth.RegisterService;
import pap.project.auth.model.controller.register.RegisterRequest;
import pap.project.user_data.UserDataService;
import pap.project.user_stats.UserStatsRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class XXX
{
    @Autowired
    private RegisterService r;

    @Autowired
    private UserDataService u;


    final List<String> usernames = Arrays.asList(
            "NebulaFox42", "PixelRider8", "ShadowMint77", "CyberNova21", "AquaBolt19",
            "LunarSpark90", "EchoDrift12", "CrimsonByte55", "FrostWing03", "SolarBlitz81",
            "RoboPulse16", "MysticVortex72", "TurboComet44", "IronClaw58", "QuantumLynx07",
            "ZenCrawler26", "PhantomDash33", "VortexPanda91", "BlazeCircuit14", "PixelKnight67",
            "NebulaStride29", "EchoWarden50", "SilverRaven23", "CyberNomad04", "RapidFalcon63",
            "FrostByte27", "LavaWhisper88", "SkyBreaker39", "ThunderGlyph95", "CrystalGazer31",
            "IronSparrow06", "CosmoDrifter80", "LaserQuill13", "VividStreak57", "SilentRider24",
            "PhantomNova78", "BlazeRunner32", "AeroCipher09", "QuantumPulse46", "ShadowCrafter85",
            "LunarCharger17", "MysticDrake51", "PixelEcho11", "TurboGlyph70", "SolarRaptor25",
            "AquaStorm64", "NebulaDrake18", "CrimsonWarden40", "EchoBolt22", "Test123"
    );

    @PostConstruct
    public void xxx()
    {
//        for (String username : usernames) {
//            final RegisterRequest z = new RegisterRequest(username, username + "@gmail.com", "123456");
//            r.registerUser("xx", z);
//        }

//        for (int i = 1; i < 200; i++) {
//            final int x = new Random().nextInt(1, 50);
//            if (new Random().nextBoolean())
//                u.processGameEnd(x, 50, System.currentTimeMillis());
//            else
//                u.processGameEnd(50, x, System.currentTimeMillis());
//
//        }

    }
}
