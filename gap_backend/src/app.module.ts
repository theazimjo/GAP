import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ConfigModule } from '@nestjs/config';
import { AuthModule } from './modules/auth/auth.module';
import { UsersModule } from './modules/users/users.module';
import { GroupsModule } from './modules/groups/groups.module';
import { TurnsModule } from './modules/turns/turns.module';
import { PaymentsModule } from './modules/payments/payments.module';
import { CommonModule } from './modules/common/common.module';

// Entities
import { User } from './entities/user.entity';
import { Group } from './entities/group.entity';
import { Membership } from './entities/membership.entity';
import { Turn } from './entities/turn.entity';
import { Payment } from './entities/payment.entity';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    TypeOrmModule.forRoot({
      type: 'postgres',
      url: process.env.DATABASE_URL,
      entities: [User, Group, Membership, Turn, Payment],
      synchronize: true, // Should be false in production
    }),
    AuthModule,
    UsersModule,
    GroupsModule,
    TurnsModule,
    PaymentsModule,
    CommonModule,
  ],
})
export class AppModule {}
