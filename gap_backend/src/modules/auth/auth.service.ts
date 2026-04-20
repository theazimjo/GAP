import { Injectable, UnauthorizedException, BadRequestException } from '@nestjs/common';
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
}
