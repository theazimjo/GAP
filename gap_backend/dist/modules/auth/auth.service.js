"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.AuthService = void 0;
const common_1 = require("@nestjs/common");
const crypto = __importStar(require("crypto"));
const jwt_1 = require("@nestjs/jwt");
const users_service_1 = require("../users/users.service");
const common_service_1 = require("../common/common.service");
let AuthService = class AuthService {
    usersService;
    commonService;
    jwtService;
    constructor(usersService, commonService, jwtService) {
        this.usersService = usersService;
        this.commonService = commonService;
        this.jwtService = jwtService;
    }
    async register(name, phone, passwordHash) {
        const existing = await this.usersService.findByPhone(phone);
        if (existing && existing.passwordHash) {
            throw new common_1.BadRequestException('User with this phone already exists');
        }
        const user = existing
            ? await this.usersService.update(existing.id, { name, passwordHash })
            : await this.usersService.create({ name, phone, passwordHash });
        const token = this.jwtService.sign({ userId: user.id });
        return { token, user: { id: user.id, name: user.name, phone: user.phone } };
    }
    async login(phone, password) {
        const user = await this.usersService.findByPhone(phone);
        if (!user || !(await this.commonService.comparePasswords(password, user.passwordHash))) {
            throw new common_1.UnauthorizedException('Invalid phone or password');
        }
        const token = this.jwtService.sign({ userId: user.id });
        return { token, user: { id: user.id, name: user.name, phone: user.phone } };
    }
    async requestOtp(phone) {
        const otpCode = this.commonService.generateOtp();
        const otpExpiresAt = this.commonService.getOtpExpiry();
        const existing = await this.usersService.findByPhone(phone);
        if (existing) {
            await this.usersService.update(existing.id, { otpCode, otpExpiresAt });
        }
        else {
            await this.usersService.create({
                phone,
                name: 'New User',
                passwordHash: '',
                otpCode,
                otpExpiresAt
            });
        }
        return { message: 'OTP sent successfully', otpCode }; // Return for dev hack
    }
    async verifyOtp(phone, otpCode) {
        const user = await this.usersService.findByPhone(phone);
        if (!user || user.otpCode !== otpCode || !user.otpExpiresAt || user.otpExpiresAt < new Date()) {
            throw new common_1.BadRequestException('Invalid or expired OTP');
        }
        await this.usersService.update(user.id, {
            otpCode: null,
            otpExpiresAt: null
        });
        const token = this.jwtService.sign({ userId: user.id });
        return { token, user: { id: user.id, name: user.name, phone: user.phone } };
    }
    async telegramLogin(telegramData) {
        const isValid = this.verifyTelegramHash(telegramData);
        if (!isValid) {
            throw new common_1.UnauthorizedException('Invalid Telegram hash');
        }
        const { id, first_name, last_name } = telegramData;
        const name = last_name ? `${first_name} ${last_name}` : first_name;
        const telegramId = id.toString();
        // Find user by telegram ID (we might need to add telegramId to User entity)
        // For now, we'll use a hack or just map it to a unique identifier
        // Let's assume we use phone as primary, but Telegram login might not provide phone
        // Actually, Telegram Login Widget DOES provide phone if requested, but Native SDK does too.
        // For this implementation, we'll try to find by a new field if we had it, 
        // but since we only have 'phone', let's use 'tg_' + id as a virtual phone if needed,
        // or better, find by name + extra logic.
        // RECOMMENDATION: Add telegramId column to User entity.
        // For now, let's look for a user with a special "phone" or create one.
        const virtualPhone = `tg_${telegramId}`;
        let user = await this.usersService.findByPhone(virtualPhone);
        if (!user) {
            user = await this.usersService.create({
                name: name,
                phone: virtualPhone,
                passwordHash: '', // Social login has no password
            });
        }
        const token = this.jwtService.sign({ userId: user.id });
        return { token, user: { id: user.id, name: user.name, phone: user.phone } };
    }
    verifyTelegramHash(data) {
        const { hash, ...userData } = data;
        const botToken = process.env.TELEGRAM_BOT_TOKEN;
        if (!botToken)
            throw new Error('TELEGRAM_BOT_TOKEN not set');
        const secretKey = crypto.createHash('sha256')
            .update(botToken)
            .digest();
        const dataCheckString = Object.keys(userData)
            .sort()
            .map(key => `${key}=${userData[key]}`)
            .join('\n');
        const hmac = crypto.createHmac('sha256', secretKey)
            .update(dataCheckString)
            .digest('hex');
        return hmac === hash;
    }
};
exports.AuthService = AuthService;
exports.AuthService = AuthService = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [users_service_1.UsersService,
        common_service_1.CommonService,
        jwt_1.JwtService])
], AuthService);
//# sourceMappingURL=auth.service.js.map