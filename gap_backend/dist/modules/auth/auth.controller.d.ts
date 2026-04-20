import { AuthService } from './auth.service';
import { UsersService } from '../users/users.service';
import { TelegramAuthDto } from './dto/telegram-auth.dto';
export declare class AuthController {
    private authService;
    private usersService;
    constructor(authService: AuthService, usersService: UsersService);
    register(body: any): Promise<{
        token: string;
        user: {
            id: number;
            name: string;
            phone: string;
        };
    }>;
    login(body: any): Promise<{
        token: string;
        user: {
            id: number;
            name: string;
            phone: string;
        };
    }>;
    requestOtp(body: any): Promise<{
        message: string;
        otpCode: string;
    }>;
    verifyOtp(body: any): Promise<{
        token: string;
        user: {
            id: number;
            name: string;
            phone: string;
        };
    }>;
    telegramLogin(dto: TelegramAuthDto): Promise<{
        token: string;
        user: {
            id: number;
            name: string;
            phone: string;
        };
    }>;
    getMe(req: any): Promise<import("../../entities/user.entity").User | null>;
}
//# sourceMappingURL=auth.controller.d.ts.map