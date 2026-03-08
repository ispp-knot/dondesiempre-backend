package ispp.project.dondesiempre.mockEntities;

import java.util.Random;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

public class RandomBasicDataGenerator {

        private static final String[] ADJECTIVE1 = {
                        "ancient", "bright", "dark", "golden", "silver",
                        "crimson", "scarlet", "azure", "emerald", "violet",
                        "frozen", "burning", "shining", "glowing", "radiant",
                        "shadowy", "dusty", "misty", "stormy", "windy",
                        "wild", "silent", "noisy", "loud", "calm",
                        "rapid", "swift", "slow", "steady", "restless",
                        "mighty", "proud", "brave", "bold", "fierce",
                        "gentle", "loyal", "noble", "rugged", "fearless"
        };

        private static final String[] ADJECTIVE2 = {
                        "running", "jumping", "soaring", "flying", "gliding",
                        "wandering", "roaming", "hunting", "watching", "stalking",
                        "sleeping", "resting", "lurking", "hiding", "waiting",
                        "rising", "falling", "drifting", "rolling", "charging",
                        "roaring", "howling", "growling", "screaming", "calling",
                        "dancing", "spinning", "twisting", "turning", "climbing",
                        "swimming", "diving", "sprinting", "racing", "chasing",
                        "guarding", "seeking", "finding", "exploring", "patrolling"
        };

        private static final String[] ANIMALS = {
                        "tiger", "lion", "wolf", "fox", "bear",
                        "panther", "leopard", "cheetah", "lynx", "hyena",
                        "eagle", "falcon", "hawk", "owl", "raven",
                        "vulture", "sparrow", "heron", "crane", "albatross",
                        "shark", "whale", "dolphin", "octopus", "seal",
                        "otter", "walrus", "penguin", "crocodile", "alligator",
                        "cobra", "python", "viper", "lizard", "gecko",
                        "buffalo", "bison", "moose", "deer", "stallion"
        };

        private static final Random RANDOM = new Random();
        private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

        /**
         * Generates a random name given a prefix
         *
         * @param prefix prefix
         * @return a name of type prefix-brave-rolling-lizard
         */

        public static String generateRandomName(String prefix) {
                String adj1 = ADJECTIVE1[RANDOM.nextInt(ADJECTIVE1.length)];
                String adj2 = ADJECTIVE2[RANDOM.nextInt(ADJECTIVE2.length)];
                String noun = ANIMALS[RANDOM.nextInt(ANIMALS.length)];
                int number = RANDOM.nextInt(100001);

                StringBuilder result = new StringBuilder();

                if (prefix != null && !prefix.isEmpty()) {
                        result.append(prefix).append("-");
                }

                result.append(adj1)
                                .append("-")
                                .append(adj2)
                                .append("-")
                                .append(noun)
                                .append("-")
                                .append(number);

                return result.toString();
        }

        /**
         * Generates a random name
         *
         * @return a name of type brave-rolling-lizard
         */
        public static String generateRandomName() {
                return generateRandomName(null);
        }

        /**
         * Generates a random email given a prefix
         *
         * @param prefix prefix
         * @return a name of type prefix-brave-rolling-lizard@example.com
         */
        public static String generateRandomEmail(String prefix) {
                return generateRandomName(prefix) + "@example.com";
        }

        /**
         * Generates a random email
         *
         * @return a name of type brave-rolling-lizard@example.com
         */
        public static String generateRandomEmail() {
                return generateRandomEmail(null);
        }

        /**
         * Generates a random Point anywhere on Earth.
         * Longitude range: -180 to 180
         * Latitude range: -90 to 90
         *
         * @return a random Point
         */
        public static Point generateRandomPoint() {
                double longitude = -180 + 360 * RANDOM.nextDouble(); // -180 to 180
                double latitude = -90 + 180 * RANDOM.nextDouble(); // -90 to 90
                Coordinate coord = new Coordinate(longitude, latitude);
                return GEOMETRY_FACTORY.createPoint(coord);
        }

        /**
         * Generates a random phone number that satisfies the pattern:
         * 
         * <pre>
         * ^(\+\d{1,3}[- ]?)?\d{7,15}$
         * </pre>
         *
         * Examples of generated numbers:
         * <ul>
         * <li>+1 1234567</li>
         * <li>+44-9876543210</li>
         * <li>1234567890123</li>
         * </ul>
         *
         * @return a random phone number as a String
         */
        public static String generateRandomPhone() {
                StringBuilder sb = new StringBuilder();

                // 50% chance to add country code
                if (RANDOM.nextBoolean()) {
                        int countryCode = 1 + RANDOM.nextInt(999);
                        sb.append("+").append(countryCode);
                        if (RANDOM.nextBoolean()) {
                                sb.append(RANDOM.nextBoolean() ? "-" : " ");
                        }
                }

                int digits = 7 + RANDOM.nextInt(9);
                for (int i = 0; i < digits; i++) {
                        sb.append(RANDOM.nextInt(10));
                }

                return sb.toString();
        }

        /**
         * Generates a random hex color, e.g., #a3f4c1
         *
         * @return random color as String
         */
        public static String generateRandomColor() {
                int r = RANDOM.nextInt(256);
                int g = RANDOM.nextInt(256);
                int b = RANDOM.nextInt(256);
                return String.format("#%02X%02X%02X", r, g, b);
        }

        /**
         * Generates a random URL suitable for testing.
         *
         * @return random URL as String
         */
        public static String generateRandomUrl() {
                String domain = generateRandomName("site").toLowerCase();
                String tld = new String[] { "com", "net", "org", "io", "dev" }[RANDOM.nextInt(5)];
                String path = "/" + generateRandomName().toLowerCase();
                return "https://www." + domain + "." + tld + path;
        }

}
