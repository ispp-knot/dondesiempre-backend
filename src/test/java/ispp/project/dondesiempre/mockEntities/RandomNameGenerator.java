package ispp.project.dondesiempre.mockEntities;

import java.util.Random;

public class RandomNameGenerator {

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

        public static String generate(String prefix) {
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

}
