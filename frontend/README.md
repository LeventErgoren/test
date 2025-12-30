# Apartman Yönetimi Frontend (React)

Bu klasör, `demo` içindeki Spring Boot backend’i KESİNLİKLE değiştirmeden çalışan bir son kullanıcı arayüzüdür.

## Çalıştırma

1) Backend’i çalıştırın (varsayılan: `http://localhost:8080`).

2) Frontend:

```bash
cd frontend
npm install
npm run dev
```

Vite dev server, `/api` isteklerini otomatik olarak `http://localhost:8080` adresine proxy’ler (CORS sorunu yaşamamak için).

## Ortam Değişkeni (opsiyonel)

Prod ortamı veya farklı host/port için `VITE_API_BASE_URL` tanımlayabilirsiniz.

Örnek:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

## Giriş Akışı

- Yönetici girişi: kullanıcı adı + şifre
- Sakin girişi: e-posta + telefon eşleşmesi (mevcut sakin kayıtları üzerinden)
