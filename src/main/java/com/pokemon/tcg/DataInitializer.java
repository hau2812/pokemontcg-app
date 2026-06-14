package com.pokemon.tcg;

import com.pokemon.tcg.model.Banner;
import com.pokemon.tcg.model.Category;
import com.pokemon.tcg.model.Product;
import com.pokemon.tcg.repository.BannerRepository;
import com.pokemon.tcg.repository.CategoryRepository;
import com.pokemon.tcg.repository.ProductRepository;
import com.pokemon.tcg.repository.SystemSettingRepository;
import com.pokemon.tcg.model.SystemSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final BannerRepository bannerRepository;
    private final SystemSettingRepository systemSettingRepository;

    @Override
    public void run(String... args) throws Exception {
        if (systemSettingRepository.findById("gradingPolicy").isEmpty()) {
            String defaultGradingPolicy = "{\"intro\":\"Chúng tôi hỗ trợ gửi thẻ bài của bạn đến các tổ chức đánh giá uy tín hàng đầu thế giới (PSA, BGS, CGC) để bảo vệ và nâng cao giá trị bộ sưu tập của bạn.\",\"steps\":[\"**Chuẩn bị thẻ:** Cho thẻ vào bọc mềm (penny sleeve) và vỏ nhựa cứng (toploader hoặc cardsaver).\",\"**Liên hệ:** Nhắn tin cho Shop để chốt số lượng, hãng đánh giá mong muốn (PSA/BGS) và báo giá dự kiến.\",\"**Gửi thẻ:** Gửi thẻ trực tiếp tại cửa hàng hoặc gửi qua đường bưu điện (chuyển phát bảo đảm).\",\"**Nhận kết quả:** Shop sẽ lo toàn bộ thủ tục gửi đi quốc tế. Thời gian chờ từ 1-3 tháng. Khi thẻ về, shop sẽ thông báo để bạn nhận lại Slab.\"],\"phone\":\"0912.345.678\",\"facebook\":\"https://www.facebook.com/share/1NXmi8hQGQ/\",\"address\":\"CS1: 107 A Nguyễn Phong Sắc, Cầu Giấy, HN | CS2: Khương Đình, Thanh Xuân, HN | CS3: Bà Điểm, Hóc Môn, HCM\"}";
            systemSettingRepository.save(new SystemSetting("gradingPolicy", defaultGradingPolicy));
        }

        if (categoryRepository.count() > 0) {
            return; // Database already seeded
        }
        
        // Create Banners
        Banner banner1 = new Banner(
                "Sự kiện ra mắt Scarlet & Violet 151",
                "Khám phá bộ thẻ bài 151 Pokemon đầu tiên với mức giá cực kỳ hấp dẫn!",
                "https://images.unsplash.com/photo-1613771404784-3a5686aa2be3?q=80&w=1200&auto=format&fit=crop",
                null,
                true
        );
        
        Banner banner2 = new Banner(
                "Giảm giá 20% cho tất cả Booster Box",
                "Mừng tuần lễ vàng, giảm ngay 20% khi mua các loại Booster Box nguyên seal.",
                "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=1200&auto=format&fit=crop",
                null,
                true
        );

        bannerRepository.saveAll(Arrays.asList(banner1, banner2));

        // Create Categories
        Category singleCards = Category.builder()
                .name("Thẻ Lẻ (Single Cards)")
                .description("Các lá bài Pokémon đơn lẻ chất lượng cao, độ hiếm từ Rare đến Secret Rare, Ultra Rare.")
                .build();

        Category boosterBoxes = Category.builder()
                .name("Hộp Bài (Booster Box)")
                .description("Hộp nguyên seal chứa 36 gói bài (booster pack) từ các series chính thức.")
                .build();

        Category eliteTrainerBoxes = Category.builder()
                .name("Elite Trainer Box (ETB)")
                .description("Hộp quà tặng đặc biệt bao gồm sleeves, năng lượng, xúc xắc và các pack thẻ bài.")
                .build();

        Category accessories = Category.builder()
                .name("Phụ Kiện (Accessories)")
                .description("Bảo vệ tối đa cho bộ sưu tập của bạn với sleeves, toploaders, album và playmats.")
                .build();

        categoryRepository.saveAll(Arrays.asList(singleCards, boosterBoxes, eliteTrainerBoxes, accessories));

        // Create Products
        // 1. Single Cards — Mỗi lá PSA là duy nhất, tách riêng từng lá với mã cert khác nhau
        // Charizard ex Alt Art — 3 lá, cert khác nhau
        Product charizard1 = Product.builder()
                .name("Charizard ex Alt Art #199 (SV 151) — PSA 10 | Cert #92481037")
                .description("PSA 10 Gem Mint — Lá bài rồng lửa Charizard ex Special Illustration Rare từ set Pokémon 151. Cert đẹp, điểm hoàn hảo.")
                .detailedDescription("Lá bài Charizard ex phiên bản Special Illustration Rare (SIR) thuộc bản mở rộng Pokémon 151 (Scarlet & Violet). \n\n**Đánh giá từ chuyên gia PSA:** Đạt điểm tuyệt đối 10 Gem Mint. \n\n**Đặc điểm nổi bật:** Thẻ sở hữu nét vẽ ấn tượng khắc họa Charizard bay trên không với ngọn lửa sống động. Bề mặt thẻ (surface), góc (corners) và mép (edges) được bảo quản trong trạng thái nguyên bản (pristine) sau khi lấy từ pack.")
                .price(new BigDecimal("3500000"))
                .stockQuantity(1)
                .imageUrl("https://images.pokemontcg.io/sv3.5/199_hires.png")
                .additionalImages(Arrays.asList(
                    "https://images.pokemontcg.io/sv3.5/199_hires.png",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Pokémon_card_back.jpg/400px-Pokémon_card_back.jpg"
                ))
                .category(singleCards)
                .isDeleted(false)
                .isFeatured(true)
                .build();

        Product charizard2 = Product.builder()
                .name("Charizard ex Alt Art #199 (SV 151) — PSA 10 | Cert #88776655")
                .description("PSA 10 Gem Mint — Charizard ex SIR cùng set 151. Mã cert số đẹp 88-77-66-55, rất thích hợp cho collector.")
                .price(new BigDecimal("3800000"))
                .stockQuantity(1)
                .imageUrl("https://images.pokemontcg.io/sv3.5/199_hires.png")
                .category(singleCards)
                .isDeleted(false)
                .build();

        Product charizard3 = Product.builder()
                .name("Charizard ex Alt Art #199 (SV 151) — PSA 9 | Cert #70215348")
                .description("PSA 9 Mint — Charizard ex SIR set 151. Điểm 9 giá mềm hơn, phù hợp để chơi hoặc sưu tầm tiết kiệm.")
                .price(new BigDecimal("2200000"))
                .stockQuantity(1)
                .imageUrl("https://images.pokemontcg.io/sv3.5/199_hires.png")
                .category(singleCards)
                .isDeleted(false)
                .build();

        // Mew VMAX Alt Art — 2 lá
        Product mew1 = Product.builder()
                .name("Mew VMAX Alt Art #269 (Fusion Strike) — PSA 10 | Cert #61392847")
                .description("PSA 10 Gem Mint — Mew VMAX Special Alternate Art với hình ảnh ngộ nghĩnh, màu sắc rực rỡ. Cert sạch đẹp.")
                .detailedDescription("Lá bài Mew VMAX Alt Art #269 từ bộ mở rộng Fusion Strike là một trong những lá bài được săn đón nhất bởi thiết kế cực kỳ độc đáo và bắt mắt. \n\n**Đánh giá PSA:** Đạt chuẩn 10 Gem Mint (hoàn hảo). Bề mặt không xước, vân hologram rõ nét dưới ánh sáng. Slab nguyên vẹn, không có vết nứt.")
                .price(new BigDecimal("2200000"))
                .stockQuantity(1)
                .imageUrl("https://images.pokemontcg.io/swsh8/269_hires.png")
                .additionalImages(Arrays.asList(
                    "https://images.pokemontcg.io/swsh8/269_hires.png",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Pokémon_card_back.jpg/400px-Pokémon_card_back.jpg",
                    "https://assets.pokemon.com/assets/cms2/img/cards/web/SWSH8/SWSH8_EN_269.png"
                ))
                .category(singleCards)
                .isDeleted(false)
                .build();

        Product mew2 = Product.builder()
                .name("Mew VMAX Alt Art #269 (Fusion Strike) — PSA 9 | Cert #55001234")
                .description("PSA 9 Mint — Mew VMAX Alt Art Fusion Strike. Điểm 9 chất lượng tốt, giá hợp lý cho người mới sưu tầm.")
                .price(new BigDecimal("1500000"))
                .stockQuantity(1)
                .imageUrl("https://images.pokemontcg.io/swsh8/269_hires.png")
                .category(singleCards)
                .isDeleted(false)
                .build();

        // Lugia V Alt Art — 1 lá
        Product lugiaV = Product.builder()
                .name("Lugia V Alt Art #186 (Silver Tempest) — PSA 10 | Cert #40518726")
                .description("PSA 10 Gem Mint — Lugia V Alternate Art vẽ cảnh bão tố đại dương hoành tráng. Lá duy nhất trong kho.")
                .price(new BigDecimal("3500000"))
                .stockQuantity(1)
                .imageUrl("https://images.pokemontcg.io/swsh12/186_hires.png")
                .category(singleCards)
                .isDeleted(false)
                .build();

        // Giratina V Alt Art — 1 lá
        Product giratinaV = Product.builder()
                .name("Giratina V Alt Art #186 (Lost Origin) — PSA 10 | Cert #31274950")
                .description("PSA 10 Gem Mint — Tác phẩm nghệ thuật trừu tượng độc đáo bậc nhất lịch sử Pokémon TCG. Cert số đẹp.")
                .price(new BigDecimal("5000000"))
                .stockQuantity(1)
                .imageUrl("https://images.pokemontcg.io/swsh11/186_hires.png")
                .category(singleCards)
                .isDeleted(false)
                .build();

        // Umbreon VMAX Alt Art — 1 lá
        Product umbreonVmax = Product.builder()
                .name("Umbreon VMAX Alt Art #215 (Evolving Skies) — PSA 10 | Cert #10000001")
                .description("PSA 10 Gem Mint — 'Moonbreon' huyền thoại, siêu đắt đỏ nhất kỷ nguyên Sword & Shield. Cert số cực đẹp 10000001.")
                .price(new BigDecimal("15000000"))
                .stockQuantity(1)
                .imageUrl("https://images.pokemontcg.io/swsh7/215_hires.png")
                .category(singleCards)
                .isDeleted(false)
                .isFeatured(true)
                .build();

        // 2. Booster Boxes
        Product evolvingSkiesBox = Product.builder()
                .name("Evolving Skies Booster Box (36 Packs)")
                .description("Hộp bài chứa 36 pack của series Evolving Skies - set bài sở hữu nhiều thẻ Alternate Art đắt giá nhất kỷ nguyên Gen 8.")
                .price(new BigDecimal("9500000"))
                .stockQuantity(5)
                .imageUrl("https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=600")
                .category(boosterBoxes)
                .isDeleted(false)
                .build();

        Product obsidianFlamesBox = Product.builder()
                .name("Obsidian Flames Booster Box (36 Packs)")
                .description("Hộp bài Scarlet & Violet: Obsidian Flames sở hữu các lá bài hệ Tera Charizard ex vô cùng mạnh mẽ trong thi đấu.")
                .price(new BigDecimal("2800000"))
                .stockQuantity(12)
                .imageUrl("https://images.unsplash.com/photo-1613771404784-3a5686aa2be3?q=80&w=600")
                .category(boosterBoxes)
                .isDeleted(false)
                .isBestSeller(true)
                .build();

        // 3. Elite Trainer Boxes
        Product crownZenithEtb = Product.builder()
                .name("Crown Zenith Elite Trainer Box")
                .description("Hộp huấn luyện viên Crown Zenith đặc biệt chứa 10 pack Crown Zenith, 1 thẻ promo Lucian VSTAR và trọn bộ phụ kiện cao cấp.")
                .price(new BigDecimal("1250000"))
                .stockQuantity(8)
                .imageUrl("https://images.unsplash.com/photo-1541701494587-cb58502866ab?q=80&w=600")
                .category(eliteTrainerBoxes)
                .isDeleted(false)
                .build();

        Product charizardUpc = Product.builder()
                .name("Charizard Ultra-Premium Collection")
                .description("Hộp rương siêu cấp chứa 3 thẻ promo Charizard V/VMAX/VSTAR Alt Art bằng kim loại, 16 booster packs và thảm đấu playmat Charizard độc quyền.")
                .price(new BigDecimal("3100000"))
                .stockQuantity(4)
                .imageUrl("https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=600")
                .category(eliteTrainerBoxes)
                .isDeleted(false)
                .build();

        // 4. Accessories
        Product dragonShieldSleeves = Product.builder()
                .name("Bọc bài Dragon Shield Matte Black (100 Sleeves)")
                .description("Sleeves bọc bảo vệ bài cao cấp hàng đầu thế giới của hãng Dragon Shield, màu đen mờ (Matte Black), bền bỉ, dễ xào bài.")
                .price(new BigDecimal("250000"))
                .stockQuantity(50)
                .imageUrl("https://images.unsplash.com/photo-1541701494587-cb58502866ab?q=80&w=600")
                .category(accessories)
                .isDeleted(false)
                .isBestSeller(true)
                .build();

        productRepository.saveAll(Arrays.asList(
                charizard1, charizard2, charizard3, mew1, mew2, lugiaV, giratinaV, umbreonVmax,
                evolvingSkiesBox, obsidianFlamesBox,
                crownZenithEtb, charizardUpc,
                dragonShieldSleeves
        ));
    }
}
