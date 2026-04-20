#!/bin/bash

# ==========================================
# GAP Project Automated Deployment Script
# ==========================================

echo "🚀 Deployment boshlanmoqda..."

# 1. Eng oxirgi kodni Git'dan tortish
echo "📥 Git'dan yangi kod yuklanmoqda..."
git pull origin main

# 2. Serverda joyni tejash uchun Android papkasini o'chirish (ixtiyoriy, lekin so'ralgan)
if [ -d "gap_app_mobile" ]; then
    echo "🧹 Android papkasi o'chirilmoqda (serverda joy tejash uchun)..."
    rm -rf gap_app_mobile
fi

# 3. Backend papkasiga kirish (agar skript rootda bo'lsa)
cd gap_backend

# 4. Docker konteynerlarni qayta qurish va yurgizish
echo "🐳 Docker konteynerlar yangilanmoqda..."
docker-compose down
docker-compose up -d --build

# 5. Keraksiz Docker image'larni tozalash
echo "🧹 Eski Docker image'lar tozalanmoqda..."
docker image prune -f

echo "✅ Muvaffaqiyatli yakunlandi! Backend http://104.248.43.194:2030 manzilida ishlamoqda."
