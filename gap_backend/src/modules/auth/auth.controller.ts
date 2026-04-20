import { Controller, Post, Body, Get, UseGuards, Req } from '@nestjs/common';
import { AuthService } from './auth.service';
import { UsersService } from '../users/users.service';
import { JwtAuthGuard } from './guards/jwt-auth.guard';

@Controller('api/auth')
export class AuthController {
  constructor(
    private authService: AuthService,
    private usersService: UsersService,
  ) {}

  @Post('register')
  async register(@Body() body: any) {
    return this.authService.register(body.name, body.phone, body.password);
  }

  @Post('login')
  async login(@Body() body: any) {
    return this.authService.login(body.phone, body.password);
  }

  @Post('request-otp')
  async requestOtp(@Body() body: any) {
    return this.authService.requestOtp(body.phone);
  }

  @Post('verify-otp')
  async verifyOtp(@Body() body: any) {
    return this.authService.verifyOtp(body.phone, body.otpCode);
  }

  @UseGuards(JwtAuthGuard)
  @Get('me')
  async getMe(@Req() req: any) {
    return this.usersService.findById(req.user.userId);
  }
}
