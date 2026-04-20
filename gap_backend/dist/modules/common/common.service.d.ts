export declare class CommonService {
    hashPassword(password: string): Promise<string>;
    comparePasswords(password: string, hash: string): Promise<boolean>;
    generateOtp(): string;
    getOtpExpiry(): Date;
}
//# sourceMappingURL=common.service.d.ts.map