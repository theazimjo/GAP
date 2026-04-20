import { Injectable, UnauthorizedException, BadRequestException } from '@nestjs/common';
import * as crypto from 'crypto';
import { JwtService } from '@nestjs/jwt';
import { UsersService } from '../users/users.service';
import { CommonService } from '../common/common.service';

@Injectable()
export class AuthService {
  constructor(
    private usersService: UsersService,
    private commonService: CommonService,
    private jwtService: JwtService,
  ) {}

  async register(name: string, phone: string, passwordHash: string) {
    const existing = await this.usersService.findByPhone(phone);
    if (existing && existing.passwordHash) {
      throw new BadRequestException('User with this phone already exists');
    }

    const user = existing 
      ? await this.usersService.update(existing.id, { name, passwordHash })
      : await this.usersService.create({ name, phone, passwordHash });

    const token = this.jwtService.sign({ userId: user.id });
    return { token, user: { id: user.id, name: user.name, phone: user.phone } };
  }

  async login(phone: string, password: string) {
    const user = await this.usersService.findByPhone(phone);
    if (!user || !(await this.commonService.comparePasswords(password, user.passwordHash))) {
      throw new UnauthorizedException('Invalid phone or password');
    }

    const token = this.jwtService.sign({ userId: user.id });
    return { token, user: { id: user.id, name: user.name, phone: user.phone } };
  }

  async requestOtp(phone: string) {
    const otpCode = this.commonService.generateOtp();
    const otpExpiresAt = this.commonService.getOtpExpiry();

    const existing = await this.usersService.findByPhone(phone);
    if (existing) {
      await this.usersService.update(existing.id, { otpCode, otpExpiresAt });
    } else {
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

  async verifyOtp(phone: string, otpCode: string) {
    const user = await this.usersService.findByPhone(phone);
    if (!user || user.otpCode !== otpCode || !user.otpExpiresAt || user.otpExpiresAt < new Date()) {
      throw new BadRequestException('Invalid or expired OTP');
    }

    await this.usersService.update(user.id, { 
      otpCode: null as any, 
      otpExpiresAt: null as any 
    });
    
    const token = this.jwtService.sign({ userId: user.id });
    return { token, user: { id: user.id, name: user.name, phone: user.phone } };
  }

  async telegramLogin(telegramData: any) {
    const isValid = this.verifyTelegramHash(telegramData);
    if (!isValid) {
      throw new UnauthorizedException('Invalid Telegram hash');
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

  private verifyTelegramHash(data: any): boolean {
    const { hash, ...userData } = data;
    const botToken = process.env.TELEGRAM_BOT_TOKEN;
    if (!botToken) throw new Error('TELEGRAM_BOT_TOKEN not set');

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
}
