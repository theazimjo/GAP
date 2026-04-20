import { Repository } from 'typeorm';
import { User } from '../../entities/user.entity';
export declare class UsersService {
    private usersRepository;
    constructor(usersRepository: Repository<User>);
    findByPhone(phone: string): Promise<User | null>;
    findById(id: number): Promise<User | null>;
    create(data: Partial<User>): Promise<User>;
    update(id: number, data: Partial<User>): Promise<User>;
    getOrCreatePlaceholder(phone: string): Promise<User>;
}
//# sourceMappingURL=users.service.d.ts.map