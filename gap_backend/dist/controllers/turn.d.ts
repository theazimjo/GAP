import { Response } from 'express';
import { AuthRequest } from '../middleware/auth';
export declare const generateTurns: (req: AuthRequest, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const getTurns: (req: AuthRequest, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
//# sourceMappingURL=turn.d.ts.map