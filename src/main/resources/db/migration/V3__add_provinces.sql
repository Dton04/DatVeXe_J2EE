CREATE TABLE IF NOT EXISTS provinces (
    province_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    code VARCHAR(50) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Insert 63 provinces from API
INSERT INTO provinces (name, code) VALUES ('Thành phố Hà Nội', 'thanh_pho_ha_noi') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Hà Giang', 'tinh_ha_giang') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Cao Bằng', 'tinh_cao_bang') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Bắc Kạn', 'tinh_bac_kan') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Tuyên Quang', 'tinh_tuyen_quang') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Lào Cai', 'tinh_lao_cai') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Điện Biên', 'tinh_dien_bien') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Lai Châu', 'tinh_lai_chau') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Sơn La', 'tinh_son_la') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Yên Bái', 'tinh_yen_bai') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Hoà Bình', 'tinh_hoa_binh') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Thái Nguyên', 'tinh_thai_nguyen') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Lạng Sơn', 'tinh_lang_son') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Quảng Ninh', 'tinh_quang_ninh') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Bắc Giang', 'tinh_bac_giang') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Phú Thọ', 'tinh_phu_tho') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Vĩnh Phúc', 'tinh_vinh_phuc') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Bắc Ninh', 'tinh_bac_ninh') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Hải Dương', 'tinh_hai_duong') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Thành phố Hải Phòng', 'thanh_pho_hai_phong') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Hưng Yên', 'tinh_hung_yen') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Thái Bình', 'tinh_thai_binh') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Hà Nam', 'tinh_ha_nam') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Nam Định', 'tinh_nam_dinh') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Ninh Bình', 'tinh_ninh_binh') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Thanh Hóa', 'tinh_thanh_hoa') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Nghệ An', 'tinh_nghe_an') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Hà Tĩnh', 'tinh_ha_tinh') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Quảng Bình', 'tinh_quang_binh') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Quảng Trị', 'tinh_quang_tri') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Thành phố Huế', 'thanh_pho_hue') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Thành phố Đà Nẵng', 'thanh_pho_da_nang') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Quảng Nam', 'tinh_quang_nam') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Quảng Ngãi', 'tinh_quang_ngai') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Bình Định', 'tinh_binh_dinh') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Phú Yên', 'tinh_phu_yen') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Khánh Hòa', 'tinh_khanh_hoa') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Ninh Thuận', 'tinh_ninh_thuan') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Bình Thuận', 'tinh_binh_thuan') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Kon Tum', 'tinh_kon_tum') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Gia Lai', 'tinh_gia_lai') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Đắk Lắk', 'tinh_dak_lak') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Đắk Nông', 'tinh_dak_nong') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Lâm Đồng', 'tinh_lam_dong') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Bình Phước', 'tinh_binh_phuoc') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Tây Ninh', 'tinh_tay_ninh') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Bình Dương', 'tinh_binh_duong') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Đồng Nai', 'tinh_dong_nai') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Bà Rịa - Vũng Tàu', 'tinh_ba_ria_vung_tau') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Thành phố Hồ Chí Minh', 'thanh_pho_ho_chi_minh') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Long An', 'tinh_long_an') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Tiền Giang', 'tinh_tien_giang') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Bến Tre', 'tinh_ben_tre') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Trà Vinh', 'tinh_tra_vinh') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Vĩnh Long', 'tinh_vinh_long') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Đồng Tháp', 'tinh_dong_thap') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh An Giang', 'tinh_an_giang') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Kiên Giang', 'tinh_kien_giang') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Thành phố Cần Thơ', 'thanh_pho_can_tho') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Hậu Giang', 'tinh_hau_giang') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Sóc Trăng', 'tinh_soc_trang') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Bạc Liêu', 'tinh_bac_lieu') ON CONFLICT (code) DO NOTHING;
INSERT INTO provinces (name, code) VALUES ('Tỉnh Cà Mau', 'tinh_ca_mau') ON CONFLICT (code) DO NOTHING;

-- Add province_id to stations
ALTER TABLE stations ADD COLUMN province_id BIGINT;

-- Link existing stations to newly created province using partial match
UPDATE stations s
SET province_id = p.province_id
FROM provinces p
WHERE p.name LIKE '%' || s.city || '%';

UPDATE stations SET province_id = (SELECT province_id FROM provinces LIMIT 1) WHERE province_id IS NULL; -- Fallback

-- Set NOT NULL and Add Foreign Key constraint
ALTER TABLE stations ALTER COLUMN province_id SET NOT NULL;
ALTER TABLE stations ADD CONSTRAINT fk_stations_province FOREIGN KEY (province_id) REFERENCES provinces (province_id);

-- Drop the old column
ALTER TABLE stations DROP COLUMN city;
