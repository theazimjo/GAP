import { JwtService } from '@nestjs/jwt';
import { UsersService } from '../users/users.service';
import { CommonService } from '../common/common.service';
export declare class AuthService {
    private usersService;
    private commonService;
    private jwtService;
    constructor(usersService: UsersService, commonService: CommonService, jwtService: JwtService);
    register(name: string, phone: string, passwordHash: string): Promise<{
        token: string;
        user: {
            id: number;
            name: string;
            phone: string;
        };
    }>;
    login(phone: string, password: string): Promise<{
        token: string;
        user: {
            id: number;
            name: string;
            phone: string;
        };
    }>;
    requestOtp(phone: string): Promise<{
        message: string;
        otpCode: string;
    }>;
    verifyOtp(phone: string, otpCode: string): Promise<{
        token: string;
        user: {
            id: number;
            name: string;
            phone: string;
        };
    }>;
    telegramLogin(telegramData: any): Promise<{
        token: string;
        user: {
            id: number;
            name: string;
            phone: string;
        };
    }>;
    private verifyTelegramHash;
}
//# sourceMappingURL=auth.service.d.ts.map