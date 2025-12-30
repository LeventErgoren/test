package com.example.config;

import com.example.entity.Admin;
import com.example.entity.Block;
import com.example.entity.Flat;
import com.example.entity.FlatType;
import com.example.repository.AdminRepository;
import com.example.repository.BlockRepository;
import com.example.repository.FlatRepository;
import com.example.repository.FlatTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final AdminRepository adminRepository;
    private final BlockRepository blockRepository;
    private final FlatTypeRepository flatTypeRepository;
    private final FlatRepository flatRepository;

    @Bean
    CommandLineRunner seedData() {
        return args -> {
            // Admin hesabı
            if (adminRepository.count() == 0) {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword("1234");
                adminRepository.save(admin);
            }

            // Güvenlik: Daha önce veri varsa tekrar seed etme (tekrarlı kayıt/validasyon hatası çıkarmaz)
            if (blockRepository.count() > 0 || flatRepository.count() > 0 || flatTypeRepository.count() > 0) {
                return;
            }

            // Daire tipleri
            FlatType type2plus1 = FlatType.builder()
                    .typeName("2+1")
                    .defaultDuesAmount(new BigDecimal("750"))
                    .build();
            FlatType type3plus1 = FlatType.builder()
                    .typeName("3+1")
                    .defaultDuesAmount(new BigDecimal("950"))
                    .build();
            FlatType dublex = FlatType.builder()
                    .typeName("Dublex")
                    .defaultDuesAmount(new BigDecimal("1300"))
                    .build();

            type2plus1 = flatTypeRepository.save(type2plus1);
            type3plus1 = flatTypeRepository.save(type3plus1);
            dublex = flatTypeRepository.save(dublex);

            // 5 apartman/blok
            List<Block> blocks = new ArrayList<>();
            blocks.add(Block.builder().name("Manolya Apartmanı").totalFloors(5).build());
            blocks.add(Block.builder().name("Papatya Apartmanı").totalFloors(6).build());
            blocks.add(Block.builder().name("Lale Apartmanı").totalFloors(4).build());
            blocks.add(Block.builder().name("Menekşe Apartmanı").totalFloors(7).build());
            blocks.add(Block.builder().name("Zambak Apartmanı").totalFloors(5).build());
            blocks = blockRepository.saveAll(blocks);

            // Daireler (bazıları DOLU olacak ki sakin kayıt ekranında uygun daire görünsün)
            // Not: FlatService kapasite kontrolü POST sırasında çalışıyor; burada doğrudan repo ile seed ediyoruz.
            List<Flat> flats = new ArrayList<>();
            int doorBase = 1;
            for (int b = 0; b < blocks.size(); b++) {
                Block block = blocks.get(b);

                // Her blokta örnek 8 daire
                for (int i = 0; i < 8; i++) {
                    int floor = (i / 2) + 1;
                    boolean isEmpty = (i % 4 == 0); // 0,4 boş; diğerleri dolu

                    FlatType ft = (i % 3 == 0) ? type2plus1 : (i % 3 == 1 ? type3plus1 : dublex);

                    Flat flat = Flat.builder()
                            .doorNumber(doorBase + i)
                            .floor(floor)
                            .isEmpty(isEmpty)
                            .block(block)
                            .flatType(ft)
                            .build();
                    flats.add(flat);
                }
                doorBase += 20;
            }

            flatRepository.saveAll(flats);
        };
    }
}